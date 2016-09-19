package test.threadpool;

import com.stringee.common.Config;
import org.apache.log4j.Logger;

/**
 *
 * @author Andy Dau
 */
public class Test {

	private static final Logger LOGGER = Logger.getLogger("Test");

	public static void main(String[] args) {
		Config.loadConfig();

		//create 2 workers for process jobs
		for (int i = 0; i < 2; i++) {
			CalcWorker worker = new CalcWorker("calc-worker-" + i, 2);
			worker.start();
		}

		for (int i = 0; i < 20; i++) {
			CalcJob job = new CalcJob(-1);
			job.setA(i);
			job.setB(i - 1);

			CalcWorker.pubJob(job);
		}
	}
}
