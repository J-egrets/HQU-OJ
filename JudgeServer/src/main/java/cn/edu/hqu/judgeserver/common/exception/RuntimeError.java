package cn.edu.hqu.judgeserver.common.exception;

import lombok.Data;

/**
 * @author egret
 */
@Data
public class RuntimeError extends Exception {
    private String message;
    private String stdout;
    private String stderr;

    public RuntimeError(String message, String stdout, String stderr) {
        super(message);
        this.message = message;
        this.stdout = stdout;
        this.stderr = stderr;
    }
}