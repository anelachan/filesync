package filesync;

/**
 * @author aaron
 * @date 7th April 2013
 */

/*
 * The Block maintains information about a part of the file.
 */

public class Block implements Cloneable {
	
	public String hash;
	public long offset;
	public long length;
	public byte[] bytes;
	
	Block (){
		
	}
	
	Block (byte[] b, long o,String h){
		RegisterBytes(b,o,h);
	}
	
	public void RegisterBytes (byte[] bytes, long offset,String hash){
		this.length=bytes.length;
		this.offset=offset;
		this.hash=hash;
	}
	
	
	
	public boolean equalContent(Block b){
		return hash.equals(b.hash);
	}
	
	public boolean equals (Block b){
		return hash.equals(b.hash) && offset==b.offset;
	}
	
	/*
	 * Getters and setters
	 */
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
	
	

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	protected Block clone(){
		Block nb = null;
		
		try {
			nb=(Block) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nb.hash = this.hash;
		nb.offset = this.offset;
		nb.length = this.length;
		if(this.bytes!=null)
			nb.bytes = this.bytes.clone();
		else nb.bytes=null;
		
		return nb;
	}
	
	
}
