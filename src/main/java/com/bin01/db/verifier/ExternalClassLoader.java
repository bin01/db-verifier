/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bin01.db.verifier;

import io.tesla.aether.Repository;
import io.tesla.aether.TeslaAether;
import io.tesla.aether.internal.DefaultTeslaAether;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * Helper class to load external maven dependencies.
 *
 */
public class ExternalClassLoader {
  private static final Logger log = LoggerFactory.getLogger(ExternalClassLoader.class);
  private static final Map<String, URLClassLoader> loadersMap = Maps.newHashMap();
  private static final Set<String> exclusions = Sets.newHashSet();

  public synchronized static URLClassLoader getClassLoaderForCoordinates(VerifierConfig config, String coordinates,
      String className) {
    try {
      DefaultTeslaAether aether = getAetherClient(config);
      return getClassLoaderForCoordinates(aether, coordinates);
    } catch (DependencyResolutionException | MalformedURLException e) {
      throw Throwables.propagate(e);
    }
  }

  public synchronized static URLClassLoader getClassLoaderForCoordinates(TeslaAether aether,
      String coordinate) throws DependencyResolutionException, MalformedURLException {
    URLClassLoader loader = loadersMap.get(coordinate);
    if (loader == null) {
      final CollectRequest collectRequest = new CollectRequest();
      collectRequest.setRoot(new Dependency(new DefaultArtifact(coordinate), JavaScopes.RUNTIME));
      DependencyRequest dependencyRequest =
          new DependencyRequest(collectRequest, DependencyFilterUtils.andFilter(
              DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME), new DependencyFilter() {
                @Override
                public boolean accept(DependencyNode node, List<DependencyNode> parents) {
                  if (accept(node.getArtifact())) {
                    return false;
                  }

                  for (DependencyNode parent : parents) {
                    if (accept(parent.getArtifact())) {
                      return false;
                    }
                  }

                  return true;
                }

                private boolean accept(final Artifact artifact) {
                  return exclusions.contains(artifact.getGroupId());
                }
              }));

      try {
        final List<Artifact> artifacts = aether.resolveArtifacts(dependencyRequest);

        List<URL> urls = Lists.newArrayListWithExpectedSize(artifacts.size());
        for (Artifact artifact : artifacts) {
          if (!exclusions.contains(artifact.getGroupId())) {
            urls.add(artifact.getFile().toURI().toURL());
          } else {
            log.debug("Skipped Artifact[{}]", artifact);
          }
        }

        for (URL url : urls) {
          log.info("Added URL[{}]", url);
        }

        loader =
            new URLClassLoader(urls.toArray(new URL[urls.size()]),
                ExternalClassLoader.class.getClassLoader());
        loadersMap.put(coordinate, loader);
      } catch (Exception e) {
        log.error("Unable to resolve artifacts for [{}].", dependencyRequest, e);
        throw Throwables.propagate(e);
      }
    }
    return loader;
  }

  public static DefaultTeslaAether getAetherClient(VerifierConfig config) {
    /*
     * DefaultTeslaAether logs a bunch of stuff to System.out, which is annoying. We choose to
     * disable that unless debug logging is turned on. "Disabling" it, however, is kinda
     * bass-ackwards. We copy out a reference to the current System.out, and set System.out to a
     * noop output stream. Then after DefaultTeslaAether has pulled The reference we swap things
     * back.
     * 
     * This has implications for other things that are running in parallel to this. Namely, if
     * anything else also grabs a reference to System.out or tries to log to it while we have things
     * adjusted like this, then they will also log to nothingness. Fortunately, the code that calls
     * this is single-threaded and shouldn't hopefully be running alongside anything else that's
     * grabbing System.out. But who knows.
     */
    List<String> remoteUriList = config.getRemoteRepositories();

    List<Repository> remoteRepositories = Lists.newArrayList();
    for (String uri : remoteUriList) {
      try {
        URI u = new URI(uri);
        Repository r = new Repository(uri);

        if (u.getUserInfo() != null) {
          String[] auth = u.getUserInfo().split(":", 2);
          if (auth.length == 2) {
            r.setUsername(auth[0]);
            r.setPassword(auth[1]);
          } else {
            log.warn(
                "Invalid credentials in repository URI, expecting [<user>:<password>], got [{}] for [{}]",
                u.getUserInfo(), uri);
          }
        }
        remoteRepositories.add(r);
      } catch (URISyntaxException e) {
        throw Throwables.propagate(e);
      }
    }

    if (log.isTraceEnabled() || log.isDebugEnabled()) {
      return new DefaultTeslaAether(config.getLocalRepository(),
          remoteRepositories.toArray(new Repository[remoteRepositories.size()]));
    }

    PrintStream oldOut = System.out;
    try {
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {

        }

        @Override
        public void write(byte[] b) throws IOException {

        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {

        }
      }));
      return new DefaultTeslaAether(config.getLocalRepository(),
          remoteRepositories.toArray(new Repository[remoteRepositories.size()]));
    } finally {
      System.setOut(oldOut);
    }
  }
}
