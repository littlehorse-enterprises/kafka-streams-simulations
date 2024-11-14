package io.littlehorse.simulations.stateful;

import io.littlehorse.simulations.stateful.generator.FakeDataGenerator;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(
        name = "./run.sh",
        synopsisSubcommandLabel = "<command>"
)
public class Main implements Runnable {

    @Spec
    private CommandSpec spec;

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Main())
                .addSubcommand(new FakeDataGenerator());
        System.exit(commandLine.execute(args));
    }

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Missing required subcommand");
    }
}
