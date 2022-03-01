package cn.honorsgc.honorv2.core;

public interface ErrorEnum {
    GlobalResponseEntity<String> responseEntity();
    int getStatus();

    default ErrorEnum setErrorMsg(String message){
        ErrorEnum base=this;
        return new ErrorEnum() {
            @Override
            public GlobalResponseEntity<String> responseEntity() {
                GlobalResponseEntity<String> response = base.responseEntity();
                response.setMessage(message);
                return response;
            }

            @Override
            public int getStatus() {
                return base.getStatus();
            }

            @Override
            public ErrorEnum setErrorMsg(String message){
                return base.setErrorMsg(message);
            }
        };
    }
}
