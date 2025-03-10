package io.littlehorse.simulations.stateful;

import io.littlehorse.simulations.stateful.app.App;
import io.littlehorse.simulations.stateful.generator.Generator;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(
        name = "./run.sh",
        synopsisSubcommandLabel = "<command>"
)
@Slf4j
public class Main implements Runnable {

    @Spec
    private CommandSpec spec;

    public static void main(String[] args) {
        log.info("<<STARTING APPLICATION>>");
        final CommandLine commandLine = new CommandLine(new Main())
                .addSubcommand(new Generator())
                .addSubcommand(new App());
        System.exit(commandLine.execute(args));
    }

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Missing required subcommand");
    }
}
