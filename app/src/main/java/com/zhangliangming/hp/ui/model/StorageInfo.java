package com.zhangliangming.hp.ui.model;

import java.io.Serializable;

public class StorageInfo  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String path;
	public String state;
	public boolean isRemoveable;
 
	public StorageInfo(String path) {
		this.path = path;
	}
 
	public boolean isMounted() {
		return "mounted".equals(state);
	}
}