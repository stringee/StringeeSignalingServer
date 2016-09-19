package test.threadpool;

import org.apache.log4j.Logger;
import com.stringee.threadpool.ThreadPoolJob;
import com.stringee.threadpool.ThreadPoolWorker;

/**
 *
 * @author Andy Dau
 */
public class CalcWorker extends ThreadPoolWorker {

	private static final Logger LOGGER = Logger.getLogger("CalcWorker");

	public CalcWorker(String name, int processJobTimeOut) {
		super(name, processJobTimeOut);
	}

	public static void pubJob(ThreadPoolJob threadPoolJob) {
		ThreadPoolWorker.pubJob(threadPoolJob, CalcWorker.class);
	}

}
