Demo1

```groovy
package com.ctrip.train.zbooking.order.biz.booking.grab.smart.reschedule.impl

import com.ctrip.soa.train.trainordercentreservice.v1.OrderDetailResponseType
import com.ctrip.soa.train.trainordercentreservice.v1.OrderMasterInfo
import com.ctrip.soa.train.trainordercentreservice.v1.PassengerModel
import com.ctrip.soa.train.trainordercentreservice.v1.RealTicketDetailInfo
import com.ctrip.train.zbooking.business.enums.ResultCodeEnum
import com.ctrip.train.zbooking.business.enums.ServiceCodeEnum
import com.ctrip.train.zbooking.order.adapter.tyorder.ITblZhixingGrabConfirmRecordService
import com.ctrip.train.zbooking.order.adapter.tyorder.ITblZhixingGrabOrderService
import com.ctrip.train.zbooking.order.agent.ordercenter.IOrderCenterCoreService
import com.ctrip.train.zbooking.order.agent.smarttrip.ISmartTripService
import com.ctrip.train.zbooking.order.biz.common.helpers.SmartOrderHelper
import com.ctrip.train.zbooking.order.biz.common.helpers.SmartOrderOutBoundHelper
import com.ctrip.train.zbooking.order.infrastructure.ab.ABUtil
import com.ctrip.train.zbooking.order.infrastructure.qmq.QmqUtil
import com.ctrip.train.zbooking.order.models.dto.soa.grabbooking.request.GetSmartRescheduleConfirmDetailRequestDTO
import com.ctrip.train.zbooking.order.models.dto.soa.grabbooking.response.GetSmartRescheduleConfirmDetailResponseDTO
import com.ctrip.train.zbooking.order.models.enums.GrabConfirmStatusEnum
import com.ctrip.train.zbooking.order.models.po.tyorder.TblZhixingGrabConfirmRecord
import com.ctrip.train.zbooking.order.models.po.tyorder.TblZhixingGrabOrder
import com.ctrip.train.ztrain.common.framework.zut.base.ZutDataMockUtil
import com.ctrip.train.ztrain.common.framework.zut.spock.ZutSpock
import com.ctriposs.baiji.rpc.mobile.common.types.MobileRequestHead
import com.google.common.collect.Lists
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.support.membermodification.MemberMatcher
import org.powermock.api.support.membermodification.MemberModifier
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor
import spock.lang.Unroll

import java.lang.reflect.Method

/**
 * 智能改签 确认详情页 Test
 *
 * @author Adam
 * @since 2023/2/9
 */
@PrepareForTest([SmartRescheduleConfirmDetailServiceImpl, QmqUtil])
@SuppressStaticInitializationFor(["com.ctrip.train.zbooking.order.infrastructure.qmq.QmqUtil"])
class SmartRescheduleConfirmDetailServiceImplTest extends ZutSpock {

    def service = new SmartRescheduleConfirmDetailServiceImpl()

    def orderCenterCoreService = Mock(IOrderCenterCoreService)
    def grabOrderService = Mock(ITblZhixingGrabOrderService)
    def confirmRecordService = Mock(ITblZhixingGrabConfirmRecordService)
    def smartOrderOutBoundHelper = Mock(SmartOrderOutBoundHelper)
    def smartTripService = Mock(ISmartTripService)
    def smartOrderHelper = Mock(SmartOrderHelper)

    void setup() {
        service.orderCenterCoreService = orderCenterCoreService
        service.grabOrderService = grabOrderService
        service.confirmRecordService = confirmRecordService
        service.smartOrderOutBoundHelper = smartOrderOutBoundHelper
        service.smartTripService = smartTripService
        service.smartOrderHelper = smartOrderHelper

        // 项目特有工具类Mock
        PowerMockito.mockStatic(QmqUtil.class)
    }

    @Unroll
    def "测试私有方法 + void方法 sendEnterPageNotice"() {
        given: "反射获取私有方法"
        Method method = service.getClass().getDeclaredMethod("sendEnterPageNotice", TblZhixingGrabOrder.class)
        method.setAccessible(true)

        and: "mock grab order"
        def grabOrder = Mock(TblZhixingGrabOrder)

        when: "反射调用私有方法"
        method.invoke(service, grabOrder)

        then: "判断Void方法内部是否调用"
        1 * grabOrder.getOriginOrderNumber()
        1 * grabOrder.getOrderNumber()
    }

    @Unroll
    def "获取智能改签确认详情页 - GetSmartRescheduleConfirmDetail"() {
        given: "mock data"
        def orderDetail = new OrderDetailResponseType(orderMaster: new OrderMasterInfo(), passengers: Lists.newArrayList(new PassengerModel()))

        grabOrderService.query(_, _) >> grabOrder
        confirmRecordService.queryWaitingConfirmOrder(_) >> rescheduleConfirmRecord
        orderCenterCoreService.orderDetailByOrderNum(_) >> orderDetail
        MemberModifier.stub(MemberMatcher.method(SmartRescheduleConfirmDetailServiceImpl.class, "getSmartType", GetSmartRescheduleConfirmDetailRequestDTO.class, OrderDetailResponseType.class)).toReturn(smartType)
        confirmRecordService.query(_, GrabConfirmStatusEnum.HAS_CONFIRMED.getValue()) >> records
        confirmRecordService.query(_, GrabConfirmStatusEnum.WAITING_CONFIRM.getValue()) >> confirmRecords
        MemberModifier.stub(MemberMatcher.method(SmartRescheduleConfirmDetailServiceImpl.class, "buildConfirmDetail", OrderDetailResponseType.class, TblZhixingGrabConfirmRecord.class, TblZhixingGrabOrder.class, boolean.class)).toReturn(confirmDetailResponse)
        MemberModifier.stub(MemberMatcher.method(SmartRescheduleConfirmDetailServiceImpl.class, "sendEnterPageNotice", TblZhixingGrabOrder.class)).toReturn(void)

        when: "call getSmartRescheduleConfirmDetail"
        def response = service.getSmartRescheduleConfirmDetail(new GetSmartRescheduleConfirmDetailRequestDTO())

        then: "response test"
        with(response) {
            resultCode == resultCodeT
            confirmStatus == confirmStatusT
            timeoutFlag == timeoutFlagT
        }

        where: "table test"
        grabOrder                 | rescheduleConfirmRecord | records                             | smartType | confirmRecords                      | confirmDetailResponse                            | resultCodeT                               | confirmStatusT                                 | timeoutFlagT
        null                      | null                    | null                                | 0         | null                                | null                                             | ServiceCodeEnum.ORDER_NOT_EXIST.getCode() | null                                           | false
        new TblZhixingGrabOrder() | null                    | [new TblZhixingGrabConfirmRecord()] | 0         | null                                | null                                             | 0                                         | GrabConfirmStatusEnum.HAS_CONFIRMED.getValue() | false
        new TblZhixingGrabOrder() | null                    | null                                | 0         | null                                | null                                             | 0                                         | null                                           | true
        new TblZhixingGrabOrder() | null                    | null                                | 37        | null                                | null                                             | 0                                         | null                                           | true
        new TblZhixingGrabOrder() | null                    | null                                | 37        | [new TblZhixingGrabConfirmRecord()] | new GetSmartRescheduleConfirmDetailResponseDTO() | ResultCodeEnum.SUCCESS.getCode()          | null                                           | false
    }

    @Unroll
    def "获取智能改签确认详情页- 异常测试 - GetSmartRescheduleConfirmDetail"() {
        given: "mock data"
        grabOrderService.query(_, _) >> new TblZhixingGrabOrder()
        confirmRecordService.queryWaitingConfirmOrder(_) >> new TblZhixingGrabConfirmRecord()
        orderCenterCoreService.orderDetailByOrderNum(_) >> orderDetail
        MemberModifier.stub(MemberMatcher.method(SmartRescheduleConfirmDetailServiceImpl.class, "buildConfirmDetail", OrderDetailResponseType.class, TblZhixingGrabConfirmRecord.class, TblZhixingGrabOrder.class, boolean.class)).toReturn(confirmDetailResponse)
        MemberModifier.stub(MemberMatcher.method(SmartRescheduleConfirmDetailServiceImpl.class, "sendEnterPageNotice", TblZhixingGrabOrder.class)).toReturn(void)

        when: "call getSmartRescheduleConfirmDetail"
        service.getSmartRescheduleConfirmDetail(new GetSmartRescheduleConfirmDetailRequestDTO())

        then: "response test"
        def exception = thrown(expectedException)
        exception.message == messageT

        where: "table test"
        orderDetail                                                     | confirmDetailResponse || expectedException        | messageT
        null                                                            | null                  || IllegalArgumentException | "origin ordercenter detail is null"
        new OrderDetailResponseType()                                   | null                  || IllegalArgumentException | "origin ordercentre master detail is null"
        new OrderDetailResponseType(orderMaster: new OrderMasterInfo()) | null                  || IllegalArgumentException | "reschedule confirm detail is null"
    }

    @Unroll
    def "SpecialSeat when+then+where"() {
        when:
        def response = service.specialSeat(seatNumber)
        then:
        response == responseT
        where:
        seatNumber || responseT
        "01"       || ABUtil.ABCODE_B
        "16"       || null
        "17"       || null
        "18"       || null
        "19"       || null
        "20"       || ABUtil.ABCODE_A
    }

    @Unroll
    def "SpecialSeat expect+where"() {
        expect:
        service.specialSeat(seatNumber) == responseT

        where:
        seatNumber || responseT
        "01"       || ABUtil.ABCODE_B
        "16"       || null
        "17"       || null
        "18"       || null
        "19"       || null
        "20"       || ABUtil.ABCODE_A
    }

//    @Unroll
//    def "GetRescheduleInfo：#passengers.passengerId"() {
//        given:
//        def orderDetail = Mock(OrderDetailResponseType)
//        orderDetail.getPassengers() >> passengers
//        orderDetail.getOrderRealTickets() >> orderRealTickets
//
//        expect:
//        service.getRescheduleInfo(orderDetail) == passportName
//
//        where:
//        passengers                                                                                                       | orderRealTickets                                                                                                                                     || passportName
//        [getPassengerModel(100L, 1), getPassengerModel(200L, 2)]                                                         | [getRealTicketDetailInfo("01", 100L), getRealTicketDetailInfo("02", 200L)]                                                                           || "测试用户2"
//        [getPassengerModel(100L, 1), getPassengerModel(200L, 2)]                                                         | [getRealTicketDetailInfo("01", 100L), getRealTicketDetailInfo("01", 200L)]                                                                           || "测试用户1"
//        [getPassengerModel(100L, 1), getPassengerModel(200L, 2), getPassengerModel(300L, 3)]                             | [getRealTicketDetailInfo("01", 100L), getRealTicketDetailInfo("01", 200L), getRealTicketDetailInfo("02", 300L)]                                      || "测试用户1"
//        [getPassengerModel(100L, 1), getPassengerModel(200L, 2), getPassengerModel(300L, 3), getPassengerModel(400L, 4)] | [getRealTicketDetailInfo("01", 100L), getRealTicketDetailInfo("01", 200L), getRealTicketDetailInfo("02", 300L), getRealTicketDetailInfo("02", 400L)] || "测试用户3"
//
//    }

    @Unroll
    def "Test - Zut - ZutDataMockUtil-getMockData"() {
        given:
        def mockHead = ZutDataMockUtil.getMockData(MobileRequestHead)

        when:
        def cid = mockHead.getCid()
        then:
        cid == "10032176311134301320"
    }

    @Unroll
    def "Test - Zut - ZutDataMockUtil-jsonFileMock"() {
        given:
        def mockOrderDetail = ZutDataMockUtil.jsonFileMock("data/OrderDetail.json", OrderDetailResponseType)

        when:
        def passengers = mockOrderDetail.getPassengers()
        def orderMaster = mockOrderDetail.getOrderMaster()
        then:
        passengers
        orderMaster
    }



    // helper

    def getPassengerModel(def id, def passportName) {
        return new PassengerModel(passengerId: id, passportName: "测试用户" + passportName)
    }

    def getRealTicketDetailInfo(def carriageNo, def id) {
        return new RealTicketDetailInfo(carriageNo: carriageNo, "passengerInfoId": id)
    }
}

```

Demo2

```groovy
package com.ctrip.train.zbooking.order.biz.booking.grab.tup.impl

import com.ctrip.soa.platform.members.orderindexzixqueryservice.v1.GetOrderInfoListResponseType
import com.ctrip.soa.train.trainordercentreservice.v1.OrderDetailResponseType
import com.ctrip.train.zbooking.grabbooking.service.type.TicketUpgradeOrderDetailRequestType
import com.ctrip.train.zbooking.grabbooking.service.type.TicketUpgradeOrderListRequestType
import com.ctrip.train.zbooking.order.adapter.tyorder.ITblZhixingGrabOrderService
import com.ctrip.train.zbooking.order.agent.ordercenter.IOrderCenterCoreService
import com.ctrip.train.zbooking.order.agent.platform.IOrderIndexZhiXingQuery
import com.ctrip.train.zbooking.order.biz.booking.grab.tup.context.TicketUpgradeConfig
import com.ctrip.train.zbooking.order.biz.booking.grab.tup.handler.MultiPassengerTicketUpgradeOrderListHandler
import com.ctrip.train.zbooking.order.biz.booking.grab.tup.handler.SinglePassengerTicketUpgradeOrderListHandler
import com.ctrip.train.zbooking.order.biz.booking.grab.tup.handler.TicketUpgradeOrderListHandler
import com.ctrip.train.zbooking.order.biz.booking.grab.tup.helper.TicketUpgradeHelper
import com.ctrip.train.zbooking.order.infrastructure.credis.ZBookingCacheManage
import com.ctrip.train.zbooking.order.infrastructure.credis.ZBookingCacheUtil
import com.ctrip.train.zbooking.order.models.constant.QConfigKey
import com.ctrip.train.zbooking.order.models.po.tyorder.TblZhixingGrabOrder
import com.ctrip.train.ztrain.common.framework.config.QConfigUtil
import com.ctrip.train.ztrain.common.framework.context.TrnContext
import com.ctrip.train.ztrain.common.framework.serialize.json.JsonUtil
import com.ctrip.train.ztrain.common.framework.zut.base.ZutDataMockUtil
import com.ctrip.train.ztrain.common.framework.zut.spock.ZutSpock
import com.ctrip.train.ztrainbooking.order.facade.ext.service.TicketUpgradeEntryInnerRequestType
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor
import org.powermock.reflect.Whitebox
import spock.lang.Unroll

/**
 * @author Adam
 * @since 2023/4/17
 */
@PrepareForTest([TicketUpgradeServiceImpl, ZBookingCacheManage])
@SuppressStaticInitializationFor(["com.ctrip.train.zbooking.order.infrastructure.credis.ZBookingCacheManage"])
class TicketUpgradeServiceImplTest extends ZutSpock {

    def service = new TicketUpgradeServiceImpl()

    def ticketUpgradeHelper = Spy(TicketUpgradeHelper)
    def iOrderIndexZhiXingQuery = Mock(IOrderIndexZhiXingQuery)
    def grabOrderService = Mock(ITblZhixingGrabOrderService)
    def orderCenterCoreService = Mock(IOrderCenterCoreService)

    void setup() {
        service.ticketUpgradeHelper = ticketUpgradeHelper
        service.iOrderIndexZhiXingQuery = iOrderIndexZhiXingQuery
        service.grabOrderService = grabOrderService
        service.orderCenterCoreService = orderCenterCoreService

        def cache = [:]
        cache[TicketUpgradeOrderListHandler.MULTI_PASSENGER] = new MultiPassengerTicketUpgradeOrderListHandler();
        cache[TicketUpgradeOrderListHandler.SINGLE_PASSENGER] = new SinglePassengerTicketUpgradeOrderListHandler();
        Whitebox.getField(TicketUpgradeHelper.class, "ticketUpgradeOrderListHandlerCache").set(ticketUpgradeHelper, cache)

        
        // 项目特有工具类Mock
        PowerMockito.mockStatic(ZBookingCacheManage.class)
        PowerMockito.when(ZBookingCacheManage.getInstance()).thenReturn(PowerMockito.mock(ZBookingCacheUtil.class));
    }

    @Unroll
    def "TupOrderList"() {
        given:
        PowerMockito.when(ZBookingCacheManage.getInstance().Get(ArgumentMatchers.anyString())).thenReturn("adam_zhengfeifei")

        def mockTicketUpgradeConfig = ZutDataMockUtil.jsonFileMock("data/TicketUpgradeConfig.json", TicketUpgradeConfig)
        PowerMockito.when(QConfigUtil.getT(QConfigKey.TICKET_UPGRADE_G_CONFIG, TicketUpgradeConfig.class)).thenReturn(mockTicketUpgradeConfig)

        def oiResponse = ZutDataMockUtil.jsonFileMock("data/OiOrderList.json", GetOrderInfoListResponseType)
        iOrderIndexZhiXingQuery.getOrderInfoList(_) >> Optional.of(oiResponse);


        when:
        def response = service.tupOrderList(new TicketUpgradeOrderListRequestType())

        then:
        response.getOrderList()
        println JsonUtil.object2Json(response)
    }

    def "TupProducts"() {
        given:
        def grabOrder = new TblZhixingGrabOrder(orderNumber: "23117599447", originOrderNumber: "23117565914", maxTicketPrice: BigDecimal.valueOf(19L))
        grabOrderService.query(_, _) >> grabOrder

        def mockOrderDetail = ZutDataMockUtil.jsonFileMock("data/OrderDetailForTup.json", OrderDetailResponseType)
        orderCenterCoreService.orderDetailByOrderNum(_) >> mockOrderDetail

        when:
        def response = service.tupOrderDetail(new TicketUpgradeOrderDetailRequestType(orderNumber: "23117599447"))

        then:
        response.headInfo
        println JsonUtil.object2Json(response)
    }

    def "TupOrderDetail"() {
    }

    def "EnhanceOrderDetailResponseDTO"() {
    }

    def "tupEntryInner" () {
        given:
        def oiResponse = ZutDataMockUtil.jsonFileMock("data/OiOrderListEntry.json", GetOrderInfoListResponseType)
        iOrderIndexZhiXingQuery.getOrderInfoList(_) >> Optional.of(oiResponse);
        def request = JsonUtil.json2Object("{\"fromPage\":\"tupOdTba\",\"orderNumber\":\"24196226426\",\"uid\":\"ZX4234248395\",\"reqChannel\":\"ZXI\",\"reqPartner\":\"zhixing\",\"version\":2}", TicketUpgradeEntryInnerRequestType)
        def context = TrnContext.getContext();
        PowerMockito.when(TrnContext.getContext().setCuid(Mockito.any())).thenReturn(context)
        PowerMockito.when(TrnContext.getContext().setPartner(Mockito.any())).thenReturn(context)
        PowerMockito.when(TrnContext.getContext().setChannel(Mockito.any())).thenReturn(context)

        when:
        def response = service.tupEntryInner(request)

        then:
        println JsonUtil.object2Json(response)
    }
}

```

Demo3

```groovy
package com.ctrip.train.zbooking.order.biz.common.helpers

import com.ctrip.train.zbooking.order.agent.booking.ITrainProductService
import com.ctrip.train.zbooking.order.agent.zxcommon.zxvip.IVipCoreService
import com.ctrip.train.zbooking.order.biz.booking.ele.create.procssor.handler.FreeLoginProductHandler
import com.ctrip.train.zbooking.order.models.dto.config.FastBookingConfigDTO
import com.ctrip.train.ztrain.common.framework.config.QConfigUtil
import com.ctrip.train.ztrain.common.framework.zut.spock.ZutSpock
import org.mockito.ArgumentMatchers
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.reflect.Whitebox
import spock.lang.Unroll

/**
 * FreeLoginProcessService Test
 *
 * @author Adam
 * @since 2023/2/16
 */
@PrepareForTest([FreeLoginProductHandler])
class FreeLoginProductHandlerTest extends ZutSpock {
    def service = new FreeLoginProductHandler()

    def trainProductService = Mock(ITrainProductService)
    def vipCoreService = Mock(IVipCoreService)

    void setup() {
        PowerMockito.field(FreeLoginProductHandler.class, "trainProductService").set(service, trainProductService)
        Whitebox.getField(FreeLoginProductHandler.class, "vipCoreService").set(service, vipCoreService)
    }

    @Unroll
    def "testQConfigUtil + 私有方法 + filterFastBookProductId"() {
        given: "Note：对比反射方式"
        def method = PowerMockito.method(FreeLoginProductHandler.class, "filterFastBookProductId", BigDecimal.class)
        PowerMockito.when(QConfigUtil.getTList(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(listData)

        expect: "反射调用私有方法"
        method.invoke(service, ticketPrice) == response

        where:
        ticketPrice        | listData                                                                                                               || response
        BigDecimal.ZERO    | null                                                                                                                   || null
        BigDecimal.ZERO    | []                                                                                                                     || null
        new BigDecimal(20) | [new FastBookingConfigDTO(lowPrice: new BigDecimal(10), highPrice: new BigDecimal(100), productId: "testProductId-1")] || "testProductId-1"
        BigDecimal.ZERO    | [new FastBookingConfigDTO(lowPrice: new BigDecimal(10), highPrice: new BigDecimal(100), productId: "testProductId-1")] || null
    }
}

```

Demo4

```groovy
package com.ctrip.train.zbooking.order.biz.booking.grab.smart.changeseat.impl

import com.ctrip.soa.train.trainordercentreservice.v1.OrderDetailResponseType
import com.ctrip.soa.train.trainordercentreservice.v1.TrainOrderComment
import com.ctrip.train.zbooking.order.adapter.tyorder.ITblZhixingGrabChangeseatRecordService
import com.ctrip.train.zbooking.order.biz.common.helpers.OrderCenterHelper
import com.ctrip.train.zbooking.order.models.dto.soa.booking.response.GetOrderDetailResponseDTO
import com.ctrip.train.ztrain.common.framework.serialize.json.JsonUtil
import com.ctrip.train.ztrain.common.framework.zut.base.ZutDataMockUtil
import com.ctrip.train.ztrain.common.framework.zut.spock.ZutSpock
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.support.membermodification.MemberMatcher
import org.powermock.api.support.membermodification.MemberModifier
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor
import spock.lang.Unroll

/**
 * @author Adam
 * @since 2023/4/19
 */
@PrepareForTest([ChangeSeatServiceImpl, OrderCenterHelper])
@SuppressStaticInitializationFor(["com.ctrip.train.zbooking.order.biz.common.helpers.OrderCenterHelper"])
class ChangeSeatServiceImplTest extends ZutSpock {

    def service = new ChangeSeatServiceImpl()

    def iTblZhixingGrabChangeseatRecordService = Mock(ITblZhixingGrabChangeseatRecordService)
    void setup() {
        service.iTblZhixingGrabChangeseatRecordService = iTblZhixingGrabChangeseatRecordService

        PowerMockito.mockStatic(OrderCenterHelper.class)
    }

    @Unroll
    def "EnhanceOrderDetailResponseDTO"() {
        given:
        def mockOrderDetail = ZutDataMockUtil.jsonFileMock("data/OrderDetailForChangeSeat.json", OrderDetailResponseType)
        def mockGetOrderDetailResponseDTO = ZutDataMockUtil.jsonFileMock("data/GetOrderDetailResponseDTO.json", GetOrderDetailResponseDTO)

        iTblZhixingGrabChangeseatRecordService.queryInitiatorOrAccept(_, _) >> Optional.empty()

        MemberModifier.stub(MemberMatcher.method(OrderCenterHelper.class, "getBackExtOrderFlag", TrainOrderComment.class, String.class)).toReturn(null)

        when:
        service.enhanceOrderDetailResponseDTO(mockGetOrderDetailResponseDTO, mockOrderDetail)

        then:
        mockGetOrderDetailResponseDTO.getOrderTicketList()
        println JsonUtil.object2Json(mockGetOrderDetailResponseDTO)
    }
}

```

