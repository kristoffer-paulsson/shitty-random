package org.example;

import org.kohsuke.args4j.Option;

public class CommandLineOptions {

    @Option(name = "--benchmark", usage = "Run the benchmark mode")
    private boolean benchmark;

    @Option(name = "--output", usage = "Specify the output file", metaVar = "OUTPUT")
    private String output;

    @Option(name = "--start", usage = "Start the daemon")
    private boolean start;

    @Option(name = "--stop", usage = "Stop the daemon")
    private boolean stop;

    @Option(name = "--restart", usage = "Restart the daemon")
    private boolean restart;

    @Option(name = "--pipe", usage = "Pipe random output to parent process")
    private boolean pipe;

    public boolean isBenchmark() {
        return benchmark;
    }

    public String getOutput() {
        return output;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isRestart() {
        return restart;
    }

    public boolean isPipe() {
        return pipe;
    }
}