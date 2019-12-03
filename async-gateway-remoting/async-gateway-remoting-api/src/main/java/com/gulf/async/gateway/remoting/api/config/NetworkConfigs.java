package com.gulf.async.gateway.remoting.api.config;

import com.gulf.async.gateway.common.config.ConfigOption;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MessageSizeEstimator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xubai on 2019/09/24 5:40 PM.
 */
public class NetworkConfigs {

    /**
     *
     * <p>
     * 对此连接禁用Nagle算法。立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，改算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。
     * Netty默认禁用该算法，从而最小化报文传输延时。
     * </p>
     */
    public static final ConfigOption<Boolean> TCP_NODELAY = ConfigOption.valueOf("TCP_NODELAY");
    public static final boolean TCP_NODELAY_DEFAULT = true;

    /**
     * 为TCP套接字设置keepalive选项时, 如果在2个小时（实际值与具体实现有关）内在
     * 任意方向上都没有跨越套接字交换数据, 则 TCP 会自动将 keepalive 探头发送到对端,
     * 此探头是对端必须响应的TCP段.
     * <p>
     * 期望的响应为以下三种之一:
     * 1. 收到期望的对端ACK响应
     * 不通知应用程序(因为一切正常), 在另一个2小时的不活动时间过后，TCP将发送另一个探头。
     * 2. 对端响应RST
     * 通知本地TCP对端已崩溃并重新启动, 套接字被关闭.
     * 3. 对端没有响
     * 套接字被关闭。
     * <p>
     * 此选项的目的是检测对端主机是否崩溃, 仅对TCP套接字有效.
     */
    public static final ConfigOption<Boolean> SO_KEEPALIVE = ConfigOption.valueOf("SO_KEEPALIVE");
    public static final boolean TCP_SO_KEEPALIVE_DEFAULT = true;

    /**
     * [TCP/IP协议详解]中描述:
     * 当TCP执行一个主动关闭, 并发回最后一个ACK ,该连接必须在TIME_WAIT状态停留的时间为2倍的MSL.
     * 这样可让TCP再次发送最后的ACK以防这个ACK丢失(另一端超时并重发最后的FIN).
     * 这种2MSL等待的另一个结果是这个TCP连接在2MSL等待期间, 定义这个连接的插口对(TCP四元组)不能再被使用.
     * 这个连接只能在2MSL结束后才能再被使用.
     * <p>
     * 许多具体的实现中允许一个进程重新使用仍处于2MSL等待的端口(通常是设置选项SO_REUSEADDR),
     * 但TCP不能允许一个新的连接建立在相同的插口对上。
     */
    public static final ConfigOption<Boolean> SO_REUSEADDR = ConfigOption.valueOf("SO_REUSEADDR");
    public static final boolean TCP_SO_REUSEADDR_DEFAULT = true;

    /**
     *
     * <p>
     * 设置snd_buf，TCP数据发送缓冲区大小。该缓冲区即TCP发送滑动窗口，linux操作系统可使用命令：cat /proc/sys/net/ipv4/tcp_smem查询其大小。
     * </p>
     */
    public static final ConfigOption<Integer> SO_SNDBUF = ConfigOption.valueOf("SO_SNDBUF");
    public static final int SO_SNDBUF_DEFAULT = 65535;

    /**
     *
     * <p>
     * 设置rcv_buf,TCP数据接收缓冲区大小。该缓冲区即TCP接收滑动窗口，linux操作系统可使用命令：cat /proc/sys/net/ipv4/tcp_rmem查询其大小。
     * 一般情况下，该值可由用户在任意时刻设置，但当设置值超过64KB时，需要在连接到远端之前设置。
     * </p>
     */
    public static final ConfigOption<Integer> SO_RCVBUF = ConfigOption.valueOf("SO_RCVBUF");
    public static final int SO_RCVBUF_DEFAULT = 65535;

    /**
     * SO_LINGER选项用来设置延迟关闭的时间，等待套接字发送缓冲区中的数据发送完成。
     * <p>
     * 没有设置该选项时，在调用close()后，在发送完FIN后会立即进行一些清理工作并返回。
     * 如果设置了SO_LINGER选项，并且等待时间为正值，则在清理之前会等待一段时间。
     */
    public static final ConfigOption<Integer> SO_LINGER = ConfigOption.valueOf("SO_LINGER");

    /**
     * 在linux内核中TCP握手过程总共会有两个队列:
     * 1) 一个俗称半连接队列, 放着那些握手一半的连接(syn queue)
     * 2) 另一个放着那些握手成功但是还没有被应用层accept的连接的队列(accept queue)
     * <p>
     * backlog控制着accept queue的大小, 但backlog的上限是somaxconn
     * linux 2.6.20版本之前 /proc/sys/net/ipv4/tcp_max_syn_backlog决定syn queue的大小,
     * 2.6.20版本之后syn queue的大小是经过一系列复杂的计算, 那个代码我看不懂...
     *
     * <pre>
     * 参考linux-3.10.28代码(socket.c):
     *
     * sock = sockfd_lookup_light(fd, &err, &fput_needed);
     * if (sock) {
     *     somaxconn = sock_net(sock->sk)->core.sysctl_somaxconn;
     *     if ((unsigned int)backlog > somaxconn)
     *         backlog = somaxconn;
     *
     *     err = security_socket_listen(sock, backlog);
     *     if (!err)
     *         err = sock->ops->listen(sock, backlog);
     *     fput_light(sock->file, fput_needed);
     * }
     *
     * 以上代码可以看到backlog并不是按照应用层所设置的backlog大小, 实际上取的是backlog和somaxconn的最小值.
     * somaxconn的值定义在:
     * /proc/sys/net/core/somaxconn
     * </pre>
     * <p>
     * 还有一点要注意, 对于TCP连接的ESTABLISHED状态, 并不需要应用层accept,
     * 只要在accept queue里就已经变成状态ESTABLISHED, 所以在使用ss或netstat排查这方面问题不要被ESTABLISHED迷惑.
     */
    public static final ConfigOption<Integer> SO_BACKLOG = ConfigOption.valueOf("SO_BACKLOG");
    public static final int TCP_SO_BACKLOG_DEFAULT = 1024;

    /**
     * Set or receive the Type-Of-Service (TOS) field that is sent with every IP packet originating from this socket.
     * It is used to prioritize packets on the network.  TOS is a byte.  Thereare some standard TOS flags
     * defined: IPTOS_LOWDELAY to mini‐mize delays for interactive traffic, IPTOS_THROUGHPUT to opti‐mize throughput,
     * IPTOS_RELIABILITY to optimize for reliabil‐ity, IPTOS_MINCOST should be used for "filler data" where slow
     * transmission doesn't matter.  At most one of these TOS values can be specified.
     * Other bits are invalid and shall be cleared.  Linux sends IPTOS_LOWDELAY datagrams first by default,
     * but the exact behavior depends on the configured queueing discipline.  Some high-priority levels may require
     * superuser privileges (the CAP_NET_ADMIN capability).
     */
    public static final ConfigOption<Integer> IP_TOS = ConfigOption.valueOf("IP_TOS");

    /**
     *
     * <p>
     * 阻止Netty在SocketChannel.read(..) 返回-1的时候自动关闭连接。
     * 一个连接的远端关闭时本地端是否关闭，默认值为False。值为False时，连接自动关闭；为True时，触发ChannelInboundHandler的userEventTriggered()方法，事件为ChannelInputShutdownEvent。
     * </p>
     */
    public static final ConfigOption<Boolean> ALLOW_HALF_CLOSURE = ConfigOption.valueOf("ALLOW_HALF_CLOSURE");

    /**
     * Netty的选项, write高水位线。写高水位标记，默认值64KB。如果Netty的写缓冲区中的字节超过该值，Channel的isWritable()返回False。
     */
    public static final ConfigOption<Integer> WRITE_BUFFER_HIGH_WATER_MARK = ConfigOption.valueOf("WRITE_BUFFER_HIGH_WATER_MARK");
    public static final int NETTY_BUFFER_HIGH_WATERMARK_DEFAULT = 64 * 1024;

    /**
     * Netty的选项, write低水位线。写低水位标记，默认值32KB。当Netty的写缓冲区中的字节超过高水位之后若下降到低水位，则Channel的isWritable()返回True。写高低水位标记使用户可以控制写入数据速度，从而实现流量控制。推荐做法是：每次调用channl.write(msg)方法首先调用channel.isWritable()判断是否可写。
     */
    public static final ConfigOption<Integer> WRITE_BUFFER_LOW_WATER_MARK = ConfigOption.valueOf("WRITE_BUFFER_LOW_WATER_MARK");
    public static final int NETTY_BUFFER_LOW_WATERMARK_DEFAULT = 32 * 1024;

    /**
     * Sets the percentage of the desired amount of time spent for I/O in the child event loops.
     * The default value is {@code 50}, which means the event loop will try to spend the same
     * amount of time for I/O as for non-I/O tasks.
     */
    public static final ConfigOption<Integer> IO_RATIO = ConfigOption.valueOf("IO_RATIO");
    public static final int NETTY_IO_RATIO_DEFAULT = 70;

    /**
     * 连接超时毫秒数，默认值30000毫秒即30秒
     */
    public static final ConfigOption<Integer> CONNECT_TIMEOUT_MILLIS = ConfigOption.valueOf("CONNECT_TIMEOUT_MILLIS");
    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 500;

    /** ==== Netty native epoll options ============================================================================ */

    /**WRITE_SPIN_COUNT
     * Set the SO_REUSEPORT option on the underlying channel. This will allow to bind multiple
     * epoll socket channels to the same port and so accept connections with multiple threads.
     * <p>
     * Be aware this method needs be called before channel#bind to have any affect.
     */
    public static final ConfigOption<Boolean> SO_REUSEPORT = ConfigOption.valueOf("SO_REUSEPORT");

    /**
     * If set, don't send out partial frames. All queued partial frames are sent when the option is cleared again.
     * This is useful for prepending headers before calling sendfile(2),
     * or for throughput optimization. As currently implemented,
     * there is a 200 millisecond ceiling on the time for which output is corked by TCP_CORK.
     * If this ceiling is reached, then queued data is automatically transmitted.
     * This option can be combined with TCP_NODELAY only since Linux 2.5.71.
     * This option should not be used in code intended to be portable.
     */
    public static final ConfigOption<Boolean> TCP_CORK = ConfigOption.valueOf("TCP_CORK");

    public static final ConfigOption<Long> TCP_NOTSENT_LOWAT = ConfigOption.valueOf("TCP_NOTSENT_LOWAT");

    /**
     * The time (in seconds) the connection needs to remain idle before TCP starts sending keepalive probes,
     * if the socket option SO_KEEPALIVE has been set on this socket.
     * This option should not be used in code intended to be portable.
     */
    public static final ConfigOption<Integer> TCP_KEEPIDLE = ConfigOption.valueOf("TCP_KEEPIDLE");

    /**
     * The time (in seconds) between individual keepalive probes.
     * This option should not be used in code intended to be portable.
     */
    public static final ConfigOption<Integer> TCP_KEEPINTVL = ConfigOption.valueOf("TCP_KEEPINTVL");

    /**
     * The maximum number of keepalive probes TCP should send before dropping the connection.
     * This option should not be used in code intended to be portable.
     */
    public static final ConfigOption<Integer> TCP_KEEPCNT = ConfigOption.valueOf("TCP_KEEPCNT");

    /**
     * This option takes an unsigned int as an argument.  When the value is greater than 0,
     * it specifies the maximum amount of time in milliseconds that transmitted data may remain
     * unacknowledged before TCP will forcibly close the corresponding connection and return ETIMEDOUT to the
     * application.  If the option value is specified as 0, TCP will to use the system default.
     * <p>
     * Increasing user timeouts allows a TCP connection to survive extended periods without end-to-end connectivity.
     * Decreasing user timeouts allows applications to "fail fast", if so desired.  Otherwise,
     * failure may take up to 20 minutes with the current system defaults in a normal WAN environment.
     * <p>
     * This option can be set during any state of a TCP connection, but is effective only during
     * the synchronized states of a connection (ESTABLISHED, FIN-WAIT-1, FIN-WAIT-2, CLOSE-WAIT,
     * CLOSING, and LAST-ACK).  Moreover, when used with the TCP keepalive (SO_KEEPALIVE) option,
     * TCP_USER_TIMEOUT will override keepalive to determine when to close a connection due
     * to keepalive failure.
     * <p>
     * The option has no effect on when TCP retransmits a packet, nor when a keepalive probe is sent.
     * <p>
     * This option, like many others, will be inherited by the socket returned by accept(2),
     * if it was set on the listening socket.
     * <p>
     * Further details on the user timeout feature can be found in
     * RFC 793 and RFC 5482 ("TCP User Timeout Option").
     */
    public static final ConfigOption<Integer> TCP_USER_TIMEOUT = ConfigOption.valueOf("TCP_USER_TIMEOUT");

    /**
     * If enabled, this boolean option allows binding to an IP address that is nonlocal or does not (yet) exist.
     * This per‐mits listening on a socket, without requiring the underlying network interface or
     * the specified dynamic IP address to be up at the time that the application is trying to bind to it.
     * This option is the per-socket equivalent of the ip_nonlo‐cal_bind /proc interface described below.
     */
    public static final ConfigOption<Boolean> IP_FREEBIND = ConfigOption.valueOf("IP_FREEBIND");

    /**
     * Setting this boolean option enables transparent proxying on this socket.
     * This socket option allows the calling applica‐tion to bind to a nonlocal IP address and operate both as a
     * client and a server with the foreign address as the local end‐point.  NOTE: this requires that routing
     * be set up in a way that packets going to the foreign address are routed through the
     * TProxy box (i.e., the system hosting the application that employs the IP_TRANSPARENT socket option).
     * Enabling this socket option requires superuser privileges (the CAP_NET_ADMIN capability).
     */
    public static final ConfigOption<Boolean> IP_TRANSPARENT = ConfigOption.valueOf("IP_TRANSPARENT");

    /**
     * Enables tcpFastOpen on the server channel. If the underlying os doesnt support TCP_FASTOPEN setting this has no
     * effect. This has to be set before doing listen on the socket otherwise this takes no effect.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7413">RFC 7413 TCP FastOpen</a>
     */
    public static final ConfigOption<Integer> TCP_FASTOPEN = ConfigOption.valueOf("TCP_FASTOPEN");

    /**
     * Set the {@code TCP_FASTOPEN_CONNECT} option on the socket. Preconditions Linux kernel 4.11 or later.
     * See
     * <a href="https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git/commit/?id=19f6d3f3">this commit</a>
     * for more details.
     */
    public static final ConfigOption<Boolean> TCP_FASTOPEN_CONNECT = ConfigOption.valueOf("TCP_FASTOPEN_CONNECT");

    /**
     * Allow a listener to be awakened only when data arrives on the socket.
     * Takes an integer value (seconds), this can bound the maximum number of
     * attempts TCP will make to complete the connection.
     * This option should not be used in code intended to be portable.
     */
    public static final ConfigOption<Integer> TCP_DEFER_ACCEPT = ConfigOption.valueOf("TCP_DEFER_ACCEPT");

    /**
     * Enable quickack mode if set or disable quickack mode if cleared.
     * In quickack mode, acks are sent immediately, rather than delayed if needed in accordance to normal TCP operation.
     * This flag is not permanent, it only enables a switch to or from quickack mode.
     * Subsequent operation of the TCP protocol will once again enter/leave quickack mode depending on internal protocol
     * processing and factors such as delayed ack timeouts occurring and data transfer.
     * This option should not be used in code intended to be portable.
     * <p>
     * TCP_QUICKACK不是永久的, 所以TCP_QUICKACK选项应该是需要在每次调用recv后重新设置的
     * Netty代码的实现可能忽略了这个问题(只设置了一次)
     */
    public static final ConfigOption<Boolean> TCP_QUICKACK = ConfigOption.valueOf("TCP_QUICKACK");

    /**
     * Default is EDGE_TRIGGERED. If you want to use #isAutoRead() {@code false} or #getMaxMessagesPerRead()
     * and have an accurate behaviour you should use LEVEL_TRIGGERED.
     * <p>
     * Be aware this config setting can only be adjusted before the channel was registered.
     */
    public static final ConfigOption<Boolean> EDGE_TRIGGERED = ConfigOption.valueOf("EDGE_TRIGGERED");

    /**
     * Sets the maximum number of messages to read per read loop.
     * If this value is greater than 1, an event loop might attempt to read multiple times to procure multiple messages.
     * <p>
     * 一次Loop读取的最大消息数，对于ServerChannel或者NioByteChannel，默认值为16，其他Channel默认值为1。
     * 默认值这样设置，是因为：ServerChannel需要接受足够多的连接，保证大吞吐量，NioByteChannel可以减少不必要的系统调用select。
     * </p>
     */
    public static final ConfigOption<Integer> MAX_MESSAGES_PER_READ = ConfigOption.valueOf("MAX_MESSAGES_PER_READ");
    public static final Integer DEFAULT_MAX_MESSAGES_PER_READ = 16;

    /**
     *
     * <p>
     * 一个Loop写操作执行的最大次数，默认值为16。也就是说，对于大数据量的写操作至多进行16次，如果16次仍没有全部写完数据，此时会提交一个新的写任务给EventLoop，
     * 任务将在下次调度继续执行。这样，其他的写请求才能被响应不会因为单个大数据量写请求而耽误。
     * </p>
     */
    public static final ConfigOption<Integer> WRITE_SPIN_COUNT = ConfigOption.valueOf("WRITE_SPIN_COUNT");
    public static final Integer DEFAULT_WRITE_SPIN_COUNT = 10;


    /**
     *
     * <p>
     * ByteBuf的分配器，默认值为ByteBufAllocator.DEFAULT，4.0版本为UnpooledByteBufAllocator，
     * 4.1版本为PooledByteBufAllocator。该值也可以使用系统参数io.netty.allocator.type配置，使用字符串值："unpooled"，"pooled"。
     * </p>
     */
    public static final ConfigOption<ByteBufAllocator> ALLOCATOR = ConfigOption.valueOf("ALLOCATOR");
    public static final ByteBufAllocator DEFAULT_ALLOCATOR = PooledByteBufAllocator.DEFAULT;


    /**
     *
     * <p>
     * 用于Channel分配接受Buffer的分配器，默认值为AdaptiveRecvByteBufAllocator.DEFAULT，是一个自适应的接受缓冲区分配器，能根据接受到的数据自动调节大小。
     * 可选值为FixedRecvByteBufAllocator，固定大小的接受缓冲区分配器。
     * </p>
     */
    public static final ConfigOption<ByteBufAllocator> RCVBUF_ALLOCATOR = ConfigOption.valueOf("RCVBUF_ALLOCATOR");
    public static final ByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = PooledByteBufAllocator.DEFAULT;

    /**
     *
     * <p>
     * 自动读取，默认值为True。Netty只在必要的时候才设置关心相应的I/O事件。对于读操作，需要调用channel.read()设置关心的I/O事件为OP_READ，这样若有数据到达才能读取以供用户处理。
     * 该值为True时，每次读操作完毕后会自动调用channel.read()，从而有数据到达便能读取；否则，需要用户手动调用channel.read()。
     * 需要注意的是：当调用config.setAutoRead(boolean)方法时，如果状态由false变为true，将会调用channel.read()方法读取数据；由true变为false，将调用config.autoReadCleared()方法终止数据读取。
     * </p>
     */
    public static final ConfigOption<Boolean> AUTO_READ = ConfigOption.valueOf("AUTO_READ");
    public static final boolean DEFAULT_AUTO_READ = true;

    /**
     *
     * <p>
     * 消息大小估算器，默认为DefaultMessageSizeEstimator.DEFAULT。估算ByteBuf、ByteBufHolder和FileRegion的大小，其中ByteBuf和ByteBufHolder为实际大小，FileRegion估算值为0。
     * 该值估算的字节数在计算水位时使用，FileRegion为0可知FileRegion不影响高低水位。
     * </p>
     */
    public static final ConfigOption<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = ConfigOption.valueOf("MESSAGE_SIZE_ESTIMATOR");
    public static final MessageSizeEstimator DEFAULT_MESSAGE_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;

    /**
     *
     * <p>
     * 单线程执行ChannelPipeline中的事件，默认值为True。该值控制执行ChannelPipeline中执行ChannelHandler的线程。
     * 如果为Trye，整个pipeline由一个线程执行，这样不需要进行线程切换以及线程同步，是Netty4的推荐做法；如果为False，ChannelHandler中的处理过程会由Group中的不同线程执行。
     * </p>
     */
    public static final ConfigOption<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = ConfigOption.valueOf("SINGLE_EVENTEXECUTOR_PER_GROUP");
    public static final boolean DEFAULT_SINGLE_EVENTEXECUTOR_PER_GROUP = true;

    /**
     * udp
     */
    public static final ConfigOption<Boolean> SO_BROADCAST = ConfigOption.valueOf("SO_BROADCAST");
    public static final boolean DEFAULT_SO_BROADCAST = false;

    public static final ConfigOption<InetAddress> IP_MULTICAST_ADDR = ConfigOption.valueOf("IP_MULTICAST_ADDR");
    public static final ConfigOption<NetworkInterface> IP_MULTICAST_IF = ConfigOption.valueOf("IP_MULTICAST_IF");
    public static final ConfigOption<Integer> IP_MULTICAST_TTL = ConfigOption.valueOf("IP_MULTICAST_TTL");
    public static final ConfigOption<Boolean> IP_MULTICAST_LOOP_DISABLED = ConfigOption.valueOf("IP_MULTICAST_LOOP_DISABLED");


    public static final ConfigOption<Boolean> NETTY_BUFFER_POOLED = ConfigOption.valueOf("gateway.netty_buffer_pooled");
    public static final boolean NETTY_BUFFER_POOLED_DEFAULT = true;

    public static final ConfigOption<Boolean> NETTY_EPOLL_SWITCH = ConfigOption.valueOf("gateway.netty_epoll_switch");
    public static final boolean NETTY_EPOLL_SWICH_DEFAULT = true;

    public static final ConfigOption<Boolean> NETTY_EPOLL_LT = ConfigOption.valueOf("gateway.netty_epoll_lt");
    public static final boolean NETTY_EPOLL_LT_DEFAULT = true;

    /**
     * TCP idle switch
     */
    public static final ConfigOption<Boolean> TCP_IDLE_SWITCH = ConfigOption.valueOf("gateway.tcp_heartbeat_switch");
    public static final boolean TCP_IDLE_SWITCH_DEFAULT = true;

    /**
     * TCP idle interval for client
     */
    public static final ConfigOption<Long> TCP_IDLE = ConfigOption.valueOf("gateway.tcp_heartbeat_interval");
    public static final long TCP_IDLE_DEFAULT = 15000L;

    /**
     * TCP idle triggered max times if no response
     */
    public static final ConfigOption<Integer> TCP_IDLE_MAXTIMES = ConfigOption.valueOf("gateway.tcp_heartbeat_maxtimes");
    public static final int TCP_IDLE_MAXTIMES_DEFAULT = 3;

    /**
     * TCP idle interval for server
     */
    public static final ConfigOption<Long> TCP_SERVER_IDLE = ConfigOption.valueOf("gateway.tcp_server_idle_interval");
    public static final long TCP_SERVER_IDLE_DEFAULT = 90000L;

    /**
     * http request max content
     */
    public static final ConfigOption<Integer> HTTP_MAX_REQUEST_CONTENT_LENGTH = ConfigOption.valueOf("gateway.http_max_request_content_length");
    public static final int HTTP_MAX_REQUEST_CONTENT_LENGTH_DEFAULT = 8192;

    /**
     * http response max content
     */
    public static final ConfigOption<Integer> HTTP_MAX_RESPONSE_CONTENT_LENGTH = ConfigOption.valueOf("gateway.http_max_response_content_length");
    public static final int HTTP_MAX_RESPONSE_CONTENT_LENGTH_DEFAULT = 1024 * 1024 * 2;

    /**
     * http
     */
    public static final ConfigOption<Integer> HTTP_MAX_INITIAL_LINE_LENGTH = ConfigOption.valueOf("gateway.http_max_initial_line_length");
    public static final int HTTP_MAX_INITIAL_LINE_LENGTH_DEFAULT = 4096;

    public static final ConfigOption<Integer> HTTP_MAX_HEADER_SIZE = ConfigOption.valueOf("gateway.http_max_header_size");
    public static final int HTTP_MAX_HEADER_SIZE_DEFAULT = 8192;

    public static final ConfigOption<Integer> HTTP_MAX_CHUNK_SIZE = ConfigOption.valueOf("gateway.http_max_chunk_size");
    public static final int HTTP_MAX_CHUNK_SIZE_DEFAULT = 8192;

    public static final ConfigOption<Boolean> HTTP_VALIDATE_HEADERS = ConfigOption.valueOf("gateway.http_validate_headers");
    public static final boolean HTTP_VALIDATE_HEADERS_DEFAULT = false;

    public static final ConfigOption<Long> HTTP_REQUEST_TIMEOUT_MILLIS = ConfigOption.valueOf("gateway.http_request_timeout_millis");
    public static final long HTTP_REQUEST_TIMEOUT_MILLIS_DEFAULT = 3000;

}
