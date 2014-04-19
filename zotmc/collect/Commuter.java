/*
 * Copyright (c) 2014, Zothf, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package zotmc.collect;

import com.google.common.base.Function;

/**
 * Bidirectional Function.
 * 
 * @author zot
 */
public interface Commuter<F, T> extends Function<F, T> {
	
	/**
	 * @return the respective input in this function for the argument. 
	 * @throws ReverseOperationException if the supplied output does not have an respective input.
	 */
	public F disapply(T output) throws ReverseOperationException;
	
	
	public static class ReverseOperationException extends Exception {
		public ReverseOperationException() {
			super();
		}
		public ReverseOperationException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
		public ReverseOperationException(String message, Throwable cause) {
			super(message, cause);
		}
		public ReverseOperationException(String message) {
			super(message);
		}
		public ReverseOperationException(Throwable cause) {
			super(cause);
		}
	}
	
}
