package com.yjl.funk.model;

/**
 * Created by Administrator on 2017/11/10.
 */

public class SearchResult {
    int error_code;
    private Search result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public Search getResult() {
        return result;
    }

    public void setResult(Search result) {
        this.result = result;
    }
}
