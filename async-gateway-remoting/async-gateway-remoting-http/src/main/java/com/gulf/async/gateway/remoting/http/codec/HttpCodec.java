package com.gulf.async.gateway.remoting.http.codec;

import com.gulf.async.gateway.remoting.api.Codec;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by xubai on 2019/09/30 5:33 PM.
 */
public class HttpCodec implements Codec {

    private int maxInitialLineLength;
    private int maxHeaderSize;
    private int maxChunkSize;
    private boolean validateHeaders;

    public HttpCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
        this.validateHeaders = validateHeaders;
    }

    @Override
    public ChannelHandler encoder() {
        return new HttpResponseEncoder();
    }

    @Override
    public ChannelHandler decoder() {
        return new HttpRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
    }
}
