package com.xavier.cmpp.protocol.handler;

import com.google.common.base.Charsets;
import com.xavier.cmpp.protocol.ClientAuthentication;
import com.xavier.cmpp.protocol.ClientManager;
import com.xavier.cmpp.protocol.FlowControl;
import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.constant.Constants;
import com.xavier.cmpp.protocol.model.CmppHead;
import com.xavier.cmpp.protocol.model.req.ActiveTest;
import com.xavier.cmpp.protocol.model.req.Connect;
import com.xavier.cmpp.protocol.model.req.Deliver;
import com.xavier.cmpp.protocol.model.req.Submit;
import com.xavier.cmpp.protocol.model.resp.ActiveTestResp;
import com.xavier.cmpp.protocol.model.resp.ConnectResp;
import com.xavier.cmpp.protocol.model.resp.DeliverResp;
import com.xavier.cmpp.protocol.model.resp.SubmitResp;
import com.xavier.cmpp.protocol.util.StatisticUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.arraycopy;

/**
 * 协议服务处理器
 *
 * @author NewGr8Player
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class CmppServerHandler extends ChannelHandlerAdapter {

    private Random random = new Random();
    private DateFormat df = new SimpleDateFormat("yyMMddHHmm");
    private DateFormat msgIdHeadFormat = new SimpleDateFormat("yyyyMMdd");
    private AtomicInteger magIdTailCount = new AtomicInteger(0);

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Autowired
    private FlowControl flowControl;

    @Autowired
    private ClientAuthentication clientAuthentication;

    @Autowired
    private ClientManager clientManager;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端关闭了连接:【{}】!", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Handler异常!连接关闭!", cause);
        ctx.close();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("收到信息:【{}】", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {

        executorService.execute(
                () -> {
                    CmppHead cmppMsg = (CmppHead) msg;
                    log.debug("Seq_id:{}", cmppMsg.getSecquenceId());
                    cmppMsg.doDecode();
                    switch (cmppMsg.getCommandId()) {
                        case CMPPConstant.APP_SUBMIT:
                            processSubmit(ctx, (Submit) cmppMsg);
                            break;
                        case CMPPConstant.APP_ACTIVE_TEST:
                            processActiveTest(ctx, (ActiveTest) cmppMsg);
                            break;
                        case CMPPConstant.CMPP_CONNECT:
                            processConnect(ctx, (Connect) cmppMsg);
                            break;
                        case CMPPConstant.APP_DELIVER_RESP:
                            processDeliverResp((DeliverResp) cmppMsg);
                            break;
                    }
                }
        );
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("客户端因长时间空闲被关闭:【{}】", ctx.channel().remoteAddress());
        ctx.close();
    }

    private void processDeliverResp(DeliverResp deliverResp) {
        log.debug("Received Deliver Resp");
    }

    private void processConnect(ChannelHandlerContext ctx, Connect connect) {
        log.debug("收到连接请求,版本:{}", connect.getVersion());
        if (connect.getVersion() != Constants.PROTOCOL_TYPE_VERSION_CMPP2 &&
                connect.getVersion() != Constants.PROTOCOL_TYPE_VERSION_CMPP3) {
            log.info("未知协议版本，关闭客户端连接:【{}】", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        boolean loginSuccess = clientAuthentication.authenticateClient(connect.getAuthenticatorSource(), connect.getTimeStamp(), ctx.channel().remoteAddress().toString());

        ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).set((int) connect.getVersion());
        ConnectResp connectResp = new ConnectResp((Integer) ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get());
        connectResp.setStatus(loginSuccess ? 0 : 3);
        connectResp.setSecquenceId(connect.getSecquenceId());
        arraycopy(connect.getAuthenticatorSource(), 0, connectResp.getAuthenticatorIsmg(), 0, 16);
        connectResp.setVersion(connect.getVersion());
        ctx.writeAndFlush(connectResp);
        if (!loginSuccess) {
            log.info("关闭不可用连接:【{}】!", ctx.channel().remoteAddress().toString());
            ctx.close();
        } else {
            clientManager.channelRegister(ctx.channel());
            log.info("登陆成功:【{}】!", ctx.channel().remoteAddress().toString());
        }
    }

    private void processActiveTest(ChannelHandlerContext ctx, ActiveTest activeTest) {
        log.debug("收到心跳:【{}】", ctx.channel().remoteAddress());
        ActiveTestResp resp = new ActiveTestResp();
        resp.setReserved((byte) 0);
        resp.setSecquenceId(activeTest.getSecquenceId());
        ctx.writeAndFlush(resp);
    }

    private void processSubmit(ChannelHandlerContext ctx, Submit submit) {
        log.debug("收到提交:【{}】", ctx.channel().remoteAddress());
        StatisticUtil.addSubmit();
        SubmitResp resp = new SubmitResp((Integer) ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get());
        resp.setSecquenceId(submit.getSecquenceId());
        ByteBuffer.wrap(resp.getMsgId()).putInt(Integer.parseInt(msgIdHeadFormat.format(Calendar.getInstance().getTime()))).putInt(magIdTailCount.incrementAndGet());
        resp.setResult(flowControl.isOverFlow() ? 8 : 0);
        ctx.writeAndFlush(resp);
        if (submit.getRegisteredDelivery() == 1 && resp.getResult() == 0) {
            sendRpt(ctx, submit, resp);
        }
        if (log.isDebugEnabled()) {
            try {
                log.debug("解码后内容:【{}】", new String(submit.getMsgContent()));
                log.trace("解码完成");
            } catch (Exception e) {
                log.warn("解码失败!");
                e.printStackTrace();
            }
        }
    }

    /**
     * 状态报告发送
     *
     * @param ctx
     * @param submit
     * @param resp
     */
    private void sendRpt(ChannelHandlerContext ctx, Submit submit, SubmitResp resp) {
        log.debug("发送状态报告:【{}】", ctx.channel().remoteAddress());
        Integer protocolType = (Integer) ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get();
        Deliver deliver = new Deliver((Integer) protocolType);
        ByteBuffer.wrap(deliver.getMsgId()).putInt(Integer.valueOf(msgIdHeadFormat.format(Calendar.getInstance().getTime()))).putInt(magIdTailCount.incrementAndGet());
        arraycopy(submit.getSrcId(), 0, deliver.getDestId(), 0, 21);
        arraycopy(submit.getServiceId(), 0, deliver.getServiceId(), 0, 10);
        deliver.setTpPid(submit.getTppId());
        deliver.setTpUdhi(submit.getTpUdhi());
        deliver.setMsgFmt((byte) 15);
        arraycopy(submit.getFeeTerminalId(), 0, deliver.getSrcTerminalId(), 0, submit.getFeeTerminalId().length);
        deliver.setSrcTerminalType((byte) 0);
        deliver.setRegisteredDelivery((byte) 1);
        arraycopy(resp.getMsgId(), 0, deliver.getMsg_Id(), 0, 8);
        arraycopy("DELIVRD".getBytes(Charsets.US_ASCII), 0, deliver.getStat(), 0, 7);
        arraycopy(df.format(new Date()).getBytes(Charsets.US_ASCII), 0, deliver.getSubmitTime(), 0, 10);
        arraycopy(deliver.getSubmitTime(), 0, deliver.getDoneTime(), 0, 10);
        random.nextBytes(deliver.getSmscSequence());
        int destTerminalIdLength = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 21 : 32;
        for (int i = 0; i < submit.getDestUsrTl(); i++) {
            StatisticUtil.addDeliver();
            if (i == 0) {
                arraycopy(submit.getDestTerminalIds(), 0, deliver.getDestTerminalId(), 0, destTerminalIdLength);
                ctx.writeAndFlush(deliver);
            } else {
                Deliver deliverTemp = deliver.clone();/* 防止原数据在未发出情况下被新数据覆盖 */
                arraycopy(submit.getDestTerminalIds(),
                        i * destTerminalIdLength, deliver.getDestTerminalId(), 0, destTerminalIdLength);
                ctx.writeAndFlush(deliverTemp);
            }
        }
    }
}
