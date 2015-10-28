
public class ConflictException extends Exception {

	public ConflictException() {
		super("Some Object is already exist there.");
	}

	public ConflictException(String message) {
		super(message);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ConflictException(Throwable cause) {
		super(cause);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ConflictException(String message, Throwable cause) {
		super(message, cause);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ConflictException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO 自動生成されたコンストラクター・スタブ
	}

}
