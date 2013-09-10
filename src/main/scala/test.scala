import org.openstreetmap.osmosis.core.cli.CommandLineParser
import org.openstreetmap.osmosis.core.TaskRegistrar
import org.openstreetmap.osmosis.core.pipeline.common.Pipeline

object Main extends App { 
	val commandLineParser = new CommandLineParser()
	// commandLineParser.parse(Array("-q", "--rx", "monaco-latest.osm.pbf","--wx", "toto.osm"))
	commandLineParser.parse(Array("-q", "--rb","monaco-latest.osm.pbf","--wp", "user=postgres", "database=osm"))
	val taskRegistrar = new TaskRegistrar()
	taskRegistrar.initialize(commandLineParser.getPlugins())
	val pipeline = new Pipeline(taskRegistrar.getFactoryRegister())
	pipeline.prepare(commandLineParser.getTaskInfoList())
	pipeline.execute()

}//--rbf file="DATA/pbf/greater-london-latest.osm.pbf" --wp user="postgres" database="osm"