package com.wong.mutzMybatis.exception;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年4月11日 上午11:08:43
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 3898167827581874507L;

	public DaoException() {
		super();
	}

	public DaoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DaoException(String msg) {
		super(msg);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}
	
}
