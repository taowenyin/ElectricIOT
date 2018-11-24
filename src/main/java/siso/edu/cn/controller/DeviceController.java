package siso.edu.cn.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import siso.edu.cn.entity.DeviceEntity;
import siso.edu.cn.entity.FlagEntity;
import siso.edu.cn.entity.ResultEntity;
import siso.edu.cn.service.DeviceService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @apiDefine deviceGroup 设备管理接口
 */
@RestController
@RequestMapping(value = "/api/manage", produces = "application/json;charset=utf-8")
public class DeviceController extends IControllerImpl {

    private DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * @api {post} /api/manage/device 创建新设备
     * @apiVersion 0.0.1
     * @apiName createDevice
     * @apiGroup deviceGroup
     *
     * @apiParam {String} imsi 手机卡IMSI
     * @apiParam {String} [uid] 自定义ID
     * @apiParam {String} [name] 设备名称
     * @apiParam {String} [serialName] 设备序列号
     * @apiParam {Number} [typeId] 设备类型ID
     * @apiParam {Number} [statusId] 设备状态ID
     * @apiParam {Number} [userId] 保管员用户ID
     * @apiParam {Number} [departmentId] 设备所属部门ID
     * @apiParam {String} [comment] 设备说明
     * @apiParam {Number} [batteryRecordGpsInterval] 电池供电时GPS采集间隔
     * @apiParam {Number} [powerRecordGpsInterval] 电源供电时GPS采集间隔
     * @apiParam {Number} [batterySendGpsInterval] 电池供电时GPS发送间隔
     * @apiParam {Number} [powerSendGpsInterval] 电源供电时GPS发送间隔
     * @apiParam {Number} [powerTcpLiveInterval] 电源供电时，TCP心跳包的发送间隔
     *
     * @apiSuccess {String} code 返回码.
     * @apiSuccess {String} msg  返回消息.
     * @apiSuccess {Object} data  JSON格式的对象.
     */
    @RequestMapping(value = "/device", method = RequestMethod.POST)
    public ResultEntity createDevice(@RequestParam("imsi") String imsi,
                                     @RequestParam(name = "uid", required = false, defaultValue = "") String uid,
                                     @RequestParam(name = "name", required = false, defaultValue = "") String name,
                                     @RequestParam(name = "serial_name", required = false, defaultValue = "") String serialName,
                                     @RequestParam(name = "type_id", required = false, defaultValue = "-1") long typeId,
                                     @RequestParam(name = "status_id", required = false, defaultValue = "-1") long statusId,
                                     @RequestParam(name = "user_id", required = false, defaultValue = "-1") long userId,
                                     @RequestParam(name = "department_id", required = false, defaultValue = "-1") long departmentId,
                                     @RequestParam(name = "comment", required = false, defaultValue = "") String comment,
                                     @RequestParam(name = "battery_record_gps_interval", required = false, defaultValue = "180") int batteryRecordGpsInterval,
                                     @RequestParam(name = "power_record_gps_interval", required = false, defaultValue = "5") int powerRecordGpsInterval,
                                     @RequestParam(name = "battery_send_gps_interval", required = false, defaultValue = "180") int batterySendGpsInterval,
                                     @RequestParam(name = "power_send_gps_interval", required = false, defaultValue = "5") int powerSendGpsInterval,
                                     @RequestParam(name = "power_tcp_live_interval", required = false, defaultValue = "10") int powerTcpLiveInterval) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<DeviceEntity> deviceEntityList = deviceService.findDeviceByImsi(imsi);

        if (deviceEntityList.size() > 0) {
            return this.createResultEntity(ResultEntity.DATA_IS_EXIST, objectMapper.convertValue(deviceEntityList.get(0), JsonNode.class));
        }

        // 获取时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 构建实体对象
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setImsi(imsi);
        deviceEntity.setIsDelete(FlagEntity.NO_DELETE);
        deviceEntity.setCreateTime(simpleDateFormat.format(new Date()));
        deviceEntity.setBatteryRecordGpsInterval(batteryRecordGpsInterval);
        deviceEntity.setPowerRecordGpsInterval(powerRecordGpsInterval);
        deviceEntity.setBatterySendGpsInterval(batterySendGpsInterval);
        deviceEntity.setPowerSendGpsInterval(powerSendGpsInterval);
        deviceEntity.setPowerTcpLiveInterval(powerTcpLiveInterval);
        if (!uid.isEmpty()) {
            deviceEntity.setUid(uid);
        }
        if (!name.isEmpty()) {
            deviceEntity.setName(name);
        }
        if (!serialName.isEmpty()) {
            deviceEntity.setSerialNumber(serialName);
        }
        if (typeId > 0) {
            deviceEntity.setTypeId(typeId);
        }
        if (statusId > 0) {
            deviceEntity.setStatusId(statusId);
        }
        if (userId > 0) {
            deviceEntity.setUserId(userId);
        }
        if (departmentId > 0) {
            deviceEntity.setDepartmentId(departmentId);
        }
        if (!comment.isEmpty()) {
            deviceEntity.setComment(comment);
        }

        // 保存并更新实体
        deviceService.save(deviceEntity);

        // 返回保存的结果
        if (deviceEntity.getId() > 0) {
            return this.createResultEntity(ResultEntity.SUCCESS, objectMapper.convertValue(deviceEntity, JsonNode.class));
        }

        return this.createResultEntity(ResultEntity.SAVE_DATA_ERROR);
    }

    /**
     * @api {delete} /api/manage/device/:id 根据ID删除设备
     * @apiVersion 0.0.1
     * @apiName deleteDeviceById
     * @apiGroup deviceGroup
     *
     * @apiParam {Number} id 设备ID
     *
     * @apiSuccess {String} code 返回码.
     * @apiSuccess {String} msg  返回消息.
     * @apiSuccess {Object} data  JSON格式的对象.
     */
    @RequestMapping(value = "/device/{id}", method = RequestMethod.DELETE)
    public ResultEntity deleteDeviceById(@PathVariable("id") long id) {
        DeviceEntity deviceEntity = deviceService.findById(id);

        if (deviceEntity == null) {
            return this.createResultEntity(ResultEntity.DELETE_ERROR);
        }

        deviceEntity.setIsDelete(FlagEntity.DELETE);
        deviceEntity = deviceService.update(deviceEntity);
        ObjectMapper objectMapper = new ObjectMapper();

        return this.createResultEntity(ResultEntity.SUCCESS, objectMapper.convertValue(deviceEntity, JsonNode.class));
    }

    /**
     * @api {put} /api/manage/device 根据ID修改设备信息
     * @apiVersion 0.0.1
     * @apiName modifyDeviceById
     * @apiGroup deviceGroup
     *
     * @apiParam {Number} id 设备ID
     * @apiParam {String} [uid] 自定义ID
     * @apiParam {String} [name] 设备名称
     * @apiParam {String} [serialName] 设备序列号
     * @apiParam {Number} [typeId] 设备类型ID
     * @apiParam {Number} [statusId] 设备状态ID
     * @apiParam {Number} [userId] 保管员用户ID
     * @apiParam {Number} [departmentId] 设备所属部门ID
     * @apiParam {String} [comment] 设备说明
     * @apiParam {Number} [batteryRecordGpsInterval] 电池供电时GPS采集间隔
     * @apiParam {Number} [powerRecordGpsInterval] 电源供电时GPS采集间隔
     * @apiParam {Number} [batterySendGpsInterval] 电池供电时GPS发送间隔
     * @apiParam {Number} [powerSendGpsInterval] 电源供电时GPS发送间隔
     * @apiParam {Number} [powerTcpLiveInterval] 电源供电时，TCP心跳包的发送间隔
     *
     * @apiSuccess {String} code 返回码.
     * @apiSuccess {String} msg  返回消息.
     * @apiSuccess {Object} data  JSON格式的对象.
     */
    @RequestMapping(value = "/device", method = RequestMethod.PUT)
    public ResultEntity modifyDeviceById(@RequestParam("id") long id,
                                     @RequestParam(name = "uid", required = false, defaultValue = "") String uid,
                                     @RequestParam(name = "name", required = false, defaultValue = "") String name,
                                     @RequestParam(name = "serial_name", required = false, defaultValue = "") String serialName,
                                     @RequestParam(name = "type_id", required = false, defaultValue = "-1") long typeId,
                                     @RequestParam(name = "status_id", required = false, defaultValue = "-1") long statusId,
                                     @RequestParam(name = "user_id", required = false, defaultValue = "-1") long userId,
                                     @RequestParam(name = "department_id", required = false, defaultValue = "-1") long departmentId,
                                     @RequestParam(name = "comment", required = false, defaultValue = "-1") String comment,
                                     @RequestParam(name = "battery_record_gps_interval", required = false, defaultValue = "180") int batteryRecordGpsInterval,
                                     @RequestParam(name = "power_record_gps_interval", required = false, defaultValue = "5") int powerRecordGpsInterval,
                                     @RequestParam(name = "battery_send_gps_interval", required = false, defaultValue = "180") int batterySendGpsInterval,
                                     @RequestParam(name = "power_send_gps_interval", required = false, defaultValue = "5") int powerSendGpsInterval,
                                     @RequestParam(name = "power_tcp_live_interval", required = false, defaultValue = "10") int powerTcpLiveInterval) {
        ObjectMapper objectMapper = new ObjectMapper();

        DeviceEntity entity = deviceService.findById(id);
        if (entity == null) {
            return this.createResultEntity(ResultEntity.NOT_FIND_ERROR);
        }

        // 构建实体对象
        entity.setBatteryRecordGpsInterval(batteryRecordGpsInterval);
        entity.setPowerRecordGpsInterval(powerRecordGpsInterval);
        entity.setBatterySendGpsInterval(batterySendGpsInterval);
        entity.setPowerSendGpsInterval(powerSendGpsInterval);
        entity.setPowerTcpLiveInterval(powerTcpLiveInterval);
        if (!uid.isEmpty()) {
            entity.setUid(uid);
        }
        if (!name.isEmpty()) {
            entity.setName(name);
        }
        if (!serialName.isEmpty()) {
            entity.setSerialNumber(serialName);
        }
        if (typeId > 0) {
            entity.setTypeId(typeId);
        }
        if (statusId > 0) {
            entity.setStatusId(statusId);
        }
        if (userId > 0) {
            entity.setUserId(userId);
        }
        if (departmentId > 0) {
            entity.setDepartmentId(departmentId);
        }
        if (!comment.isEmpty()) {
            entity.setComment(comment);
        }

        // 保存并更新实体
        entity = deviceService.update(entity);

        return this.createResultEntity(ResultEntity.SUCCESS, objectMapper.convertValue(entity, JsonNode.class));
    }

    /**
     * @api {get} /api/manage/device/:id 根据ID获取设备信息
     * @apiVersion 0.0.1
     * @apiName getDeviceById
     * @apiGroup deviceGroup
     *
     * @apiParam {Number} id 设备ID
     *
     * @apiSuccess {String} code 返回码.
     * @apiSuccess {String} msg  返回消息.
     * @apiSuccess {Object} data  JSON格式的对象.
     */
    @RequestMapping(value = "/device/{id}", method = RequestMethod.GET)
    public ResultEntity getDeviceById(@PathVariable("id") long id) {
        DeviceEntity deviceEntity = deviceService.findById(id);

        if (deviceEntity != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            return this.createResultEntity(ResultEntity.SUCCESS,
                    objectMapper.convertValue(deviceEntity, JsonNode.class));
        }

        return this.createResultEntity(ResultEntity.NOT_FIND_ERROR);
    }

    /**
     * @api {get} /api/manage/device 获取所有设备信息
     * @apiVersion 0.0.1
     * @apiName getAllDevice
     * @apiGroup deviceGroup
     *
     * @apiSuccess {String} code 返回码.
     * @apiSuccess {String} msg  返回消息.
     * @apiSuccess {Object} data  JSON格式的对象.
     */
    @RequestMapping(value = "/device", method = RequestMethod.GET)
    public ResultEntity getAllDevice() {
        List<DeviceEntity> deviceEntityList = deviceService.findAll();

        if (deviceEntityList.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            return this.createResultEntity(ResultEntity.SUCCESS,
                    objectMapper.convertValue(deviceEntityList, JsonNode.class));
        }

        return this.createResultEntity(ResultEntity.NOT_FIND_ERROR);
    }

}