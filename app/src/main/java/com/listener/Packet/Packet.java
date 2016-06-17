package com.listener.Packet;

public interface Packet
{
	public byte[] build();

	public void parse(byte[] packet);
}