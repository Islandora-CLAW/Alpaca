/*
 * Licensed to Islandora Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Islandora Foundation licenses this file to you under the MIT License.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.islandora.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.exec.ExecBinding;
import org.apache.camel.component.exec.ExecCommandExecutor;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.ObjectHelper;

/**
 * The endpoint mimics an {@link IslandoraEndpoint} to provide Islandora specific
 * functionality.
 *
 * @see IslandoraEndpoint
 */
@UriEndpoint(scheme = "islandora", title = "Islandora", syntax = "islandora:command", producerOnly = true, label = "system")
public class IslandoraEndpoint extends DefaultEndpoint {

    /**
     * Indicates that no {@link #timeout} is used.
     */
    public static final long NO_TIMEOUT = Long.MAX_VALUE;

    @UriPath @Metadata(required = "true")
    private String args;
    @UriParam
    private String workingDir;
    @UriParam
    private long timeout;
    @UriParam
    private String outFile;
    @UriParam
    private boolean useStderrOnEmptyStdout;
    @UriParam
    private ExecCommandExecutor commandExecutor;
    
    private ExecBinding binding;
    
    public IslandoraEndpoint(String uri, IslandoraComponent component) {
        super(uri, component);
        this.timeout = NO_TIMEOUT;
        this.binding = new IslandoraExecBinding();
    }

    public Producer createProducer() throws Exception {
        return new IslandoraProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Consumer not supported for IslandoraEndpoint!");
    }

    public boolean isSingleton() {
        return true;
    }

    public String getExecutable() {
        return ((IslandoraComponent)this.getComponent()).getExecutable();
    }

    public String getArgs() {
        return this.args;
    }

    /**
     * Sets the Islandora command to be executed by the Islandora script. The
     * command must not be empty or * <code>null</code>. The Islandora script
     * is automatically prefixed to the supplied args.
     *
     *  @param args
     *             the arument string
     */
    public void setArgs(String args) {
        ObjectHelper.notEmpty(args, "args");
        IslandoraComponent component = (IslandoraComponent)this.getComponent();
        this.args = component.getIslandoraScript() + " " + args;
    }
    
    public String getWorkingDir() {
        return ((IslandoraComponent)this.getComponent()).getWorkingDir();
    }

    public long getTimeout() {
        return timeout;
    }

    /**
     * The timeout, in milliseconds, after which the executable should be terminated. If execution has not completed within the timeout, the component will send a termination request.
     *
     * @param timeout
     *            the timeout, in milliseconds, after which the executable should be terminated
     */
    public void setTimeout(long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("The timeout must be a positive long!");
        }
        this.timeout = timeout;
    }

    public String getOutFile() {
        return outFile;
    }

    /**
     * The name of a file, created by the executable, that should be considered as its output.
     * If no outFile is set, the standard output (stdout) of the executable will be used instead.
     *
     * @param outFile
     *            the name of a file, created by the executable, that should be considered as its output.
     */
    public void setOutFile(String outFile) {
        ObjectHelper.notEmpty(outFile, "outFile");
        this.outFile = outFile;
    }

    public ExecCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public ExecBinding getBinding() {
        return binding;
    }

    public boolean isUseStderrOnEmptyStdout() {
        return useStderrOnEmptyStdout;
    }

    /**
     * A boolean indicating that when stdout is empty, this component will populate the Camel Message Body with stderr. This behavior is disabled (false) by default.
     *
     * @param useStderrOnEmptyStdout
     *            a boolean indicating that when stdout is empty, this component will populate the Camel Message Body with stderr
     */
    public void setUseStderrOnEmptyStdout(boolean useStderrOnEmptyStdout) {
        this.useStderrOnEmptyStdout = useStderrOnEmptyStdout;
    }
}


