package com.zype.android.webapi;

public class RequestTicket {

	private static long sNextId = 0;

	private long mId;

	private RequestTicket(long id) {
		mId = id;
	}

	public static RequestTicket newInstance() {
		return new RequestTicket(++sNextId);
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		RequestTicket other = (RequestTicket) obj;

		return mId == other.mId;

	}
	
	@Override
	public int hashCode() {
		return Long.valueOf(mId).hashCode();
	}

}
