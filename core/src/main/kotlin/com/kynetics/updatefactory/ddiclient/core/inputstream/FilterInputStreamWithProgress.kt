/*
 * Copyright © 2017-2021  Kynetics  LLC
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.kynetics.updatefactory.ddiclient.core.inputstream

import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class FilterInputStreamWithProgress(
    inputStream: InputStream,
    private val totalSize: Long,
    existingBytes: Long,
    private val initialOffset: Long = 0
) : FilterInputStream(inputStream) {

    private val bytesRead = AtomicLong(initialOffset)

    @Throws(IOException::class)
    override fun read(): Int {
        val result = super.read()
        if (result != -1) {
            bytesRead.incrementAndGet()
        }
        return result
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        val bytesReadCount = super.read(buffer, offset, length)
        if (bytesReadCount != -1) {
            bytesRead.addAndGet(bytesReadCount.toLong())
        }
        return bytesReadCount
    }

    fun getProgress(): Double {
        if (totalSize <= 0) return 0.0
        return bytesRead.get().toDouble() / totalSize
    }
    fun getBytesRead(): Long = bytesRead.get()
}
