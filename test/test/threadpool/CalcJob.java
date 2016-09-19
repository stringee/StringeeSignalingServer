package test.threadpool;

import org.apache.log4j.Logger;
import com.stringee.threadpool.ThreadPoolJob;

/**
 *
 * @author Andy Dau
 */
public class CalcJob extends ThreadPoolJob {

	private static final Logger LOGGER = Logger.getLogger("CalcJob");

	private int a;
	private int b;

	public CalcJob(int workerChooseIndex) {
		super(workerChooseIndex);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
		}
		if (timedout) {
			return;
		}

		int c = a * b;
		LOGGER.info(a + " * " + b + " = " + c);
	}

	@Override
	public void timeout() {
		timedout = true;
		LOGGER.info("timedout");
	}

	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

}
