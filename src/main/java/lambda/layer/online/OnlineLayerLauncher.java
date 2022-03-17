package lambda.layer.online;

import java.util.Properties;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import lambda.layer.online.functions.OnlineETLFunction;
import lambda.utils.Utils;

public class OnlineLayerLauncher implements Runnable {

	private static final Integer PARALLELISM = 1;

	public void run() {

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		env.setParallelism(PARALLELISM);

		// 2.1: Definir el tratamiento de los datos en tiempo real mediante Flink.
		// Para ello, necesitaremos trabajar con ventanas temporales y guardar los modelos en MongoDB.
		
		Properties props = Utils.PROPIEDADES;
		SourceFunction<String> function = new FlinkKafkaConsumer010<String>(props.getProperty("topic"), new SimpleStringSchema(), props );
		DataStream<String> stream = env.addSource(function);
		
		stream.timeWindowAll(Time.milliseconds(Utils.ONLINE_MILLIS)).process(new OnlineETLFunction());

		try {
			env.execute("Online Layer");
		} catch (Exception e) {
			throw new IllegalStateException();
		}

	}

}
