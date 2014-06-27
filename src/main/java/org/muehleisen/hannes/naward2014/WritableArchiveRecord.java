package org.muehleisen.hannes.naward2014;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCRecord;

/**
 * This is a very clumsy implementation that poorly wraps an InputStream.
 * 
 * It would make more sense to store the HDFS location of the source file 
 * and the offset of the current record.
 * 
 * This simple form could be written in and our easily, and used to set-up a 
 * valid ArchiveRecord on demand. 
 * 
 * Not clear how to set up ArchiveRecordHeader for ARC form?
 * 
 * @author Andrew.Jackson@bl.uk
 *
 */
public class WritableArchiveRecord implements Writable {
	public static int SIZE_LIMIT = 1024 * 1024 * 20; // 20MB - only applies if you try to 'getPayload' or 'write' it, otherwise streaming is used.
	
	private static Log log = LogFactory.getLog( WritableArchiveRecord.class );
	private ArchiveRecord record = null;

	public WritableArchiveRecord() {}

	public WritableArchiveRecord( ArchiveRecord record ) throws IOException {
		this.record = record;
	}

	public void setRecord( ArchiveRecord record ) throws IOException {
		this.record = record;
	}

	public ArchiveRecord getRecord() {
		log.debug( "Calling getRecord()..." );
		return record;
	}

	public byte[] getPayload( int max_buffer_size ) throws IOException {
		log.debug( "Calling getPayload( int )..." );
		BufferedInputStream input = new BufferedInputStream( record );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		int ch;
		byte[] buffer = new byte[ SIZE_LIMIT ];
		try {
			while( ( ch = input.read( buffer ) ) >= 0 ) {
				output.write( buffer, 0, ch );
			}
		} catch( IndexOutOfBoundsException i ) {
			// Invalid Content-Length throws this.
		}
		return output.toByteArray();
	}

	public InputStream getPayloadAsStream() {
		log.debug( "Calling getPayloadAsStream()..." );
		return record;
	}

	public void readFields( DataInput input ) throws IOException {
		log.debug( "Calling readField( DataInput )..." );
		record = ( WARCRecord ) input;
	}

	public void write( DataOutput output ) throws IOException {
		log.debug( "Calling write( DataOutput )..." );
		if( record != null ) {
			int ch;
			byte[] buffer = new byte[ SIZE_LIMIT ];
			while( ( ch = record.read( buffer ) ) >= 0 ) {
				output.write( buffer, 0, ch );
			}
		}
	}
}
