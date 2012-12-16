package aufgabe2.data.buffer;

import java.nio.ByteBuffer;

public interface IBufferManager {
	
	/**
	 * Gibt den Buffer mit dem angegebenen BufferKey zurück
	 * @param bufferKey
	 * @return
	 */
	ByteBuffer getBBuffer(String bufferKey);
	
	/**
	 * Tauscht einen ByteBuffer gegen einen anderen, gleichwertigen aus
	 * @param bufferKey
	 * @return
	 */
	ByteBuffer exchangeBBuffer(String bufferKey, ByteBuffer freeBuffer);
	
	/**
	 * Gibt den persistenceBuffer zurück
	 * @return
	 */
	MemPersistence getMemPersistence();
	
}
