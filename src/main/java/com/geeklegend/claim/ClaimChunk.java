package com.geeklegend.claim;

import com.geeklegend.utils.Cuboid;

public class ClaimChunk {
	private final Claim claim;
	private final Cuboid chunk;
	
	public ClaimChunk(final Claim claim, final Cuboid chunk) {
		this.claim = claim;
		this.chunk = chunk;
	}
	
	public ClaimChunk(final Cuboid chunk) {
		this(null, chunk);
	}
	
	public Claim getClaim() {
		return claim;
	}
	
	public Cuboid getChunk() {
		return chunk;
	}
}
