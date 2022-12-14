package lobbyServer.response;

public class Response_Error extends Response{
	public Response_Error(boolean isValid) {
		super(isValid);
	}

	@Override
	protected void onDenied() {
		super.onDenied();
		System.out.println("잘못된 요청입니다.!");
	}
}
