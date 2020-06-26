package vn.BengBeng.icmds.exception;

public class InvalidConfigurationException
	extends RuntimeException {
	
	public InvalidConfigurationException(String msg) {
		super(msg);
	}
	
	@Override
	public Throwable initCause(Throwable cause) {
		return initCause(cause);
	}
	
	@Override
	public Throwable fillInStackTrace() {
		return fillInStackTrace();
	}
	
}
