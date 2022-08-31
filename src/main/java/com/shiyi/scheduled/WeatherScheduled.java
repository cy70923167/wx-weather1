package com.shiyi.scheduled;

import cn.hutool.core.date.ChineseDate;
import com.google.gson.Gson;
import com.shiyi.dto.AccessTokenDTO;
import com.shiyi.dto.WeatherDTO;
import com.shiyi.vo.DataVO;
import com.shiyi.vo.PropertyVO;
import com.shiyi.vo.RequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@EnableScheduling
public class WeatherScheduled {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Gson gson;

    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Value("${wx.openId}")
    private String openId;

    @Value("${spouse.birthday}")
    private String birthday;

    @Value("${first.day.of.love}")
    private String loveDay;

    @Value("${acacia.city}")
    private String cityName;

    @Value("${birthday.chineseDate}")
    private Boolean isChineseDate;

    @Value("${message.template.id}")
    private String msgTempId;

    private static final List<String> list = new ArrayList<>();

    private int number = 0;

    SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");

    static {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("like.text");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceAsStream)));
        String content = null;
        while (true) {
            try {
                if ((content = reader.readLine()) == null) break;
            } catch (IOException e) {
                log.error("读取文件内容失败，异常信息为：{}",e.getMessage());
            }
            list.add(content);
        }
    }


    @Scheduled(cron = "${cron.set}")
    public void weather (){
        RequestVO requestVO = buildData();
        send(requestVO);
    }

    /**
     * 发送请求
     * @param requestVO 请求对象
     */
    private void send(RequestVO requestVO) {
        AccessTokenDTO accessToken = getAccessToken();

        String json = gson.toJson(requestVO);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity formEntity = new HttpEntity(json, headers);
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken.getAccess_token();
        Object result = restTemplate.postForObject(url, formEntity, Object.class);
        assert result != null;
        log.info("发送成功,结果为：{}",result);
    }

    /**
     * 组建请求参数
     * @return
     */
    private RequestVO buildData() {
        WeatherDTO weather = getWeather();
        DataVO data = new DataVO().setLove(PropertyVO.init(getLoveDay(),"#22DDB8"))
                .setCity(PropertyVO.init(weather.getCity(),"#11C2EE"))
                .setDate(PropertyVO.init(weather.getDate() + " " + weather.getWeek(),"#2BD591"))
                .setWeather(PropertyVO.init(weather.getWea(),"#FF0000"))
                .setMinTemperature(PropertyVO.init(weather.getTem_night() + " ℃","#DD4822"))
                .setMaxTemperature(PropertyVO.init(weather.getTem_day() + " ℃","#DDDD22"))
                .setHumidity(PropertyVO.init(weather.getHumidity(),"#DDB822"))
                .setWindDirection(PropertyVO.init(weather.getWin(),"#996685"))
                .setWindLevel(PropertyVO.init(weather.getWin_speed(),"#F709F7"))
                .setAirPressure(PropertyVO.init(weather.getPressure(),"#99667B"))
                .setAirQuality(PropertyVO.init(weather.getAir(),"#669999"))
                .setPresence(PropertyVO.init(list.get(number),"#22DDB8"))
                .setBirthday(PropertyVO.init(getBirthDay(birthday),"#0033FF"));
        number++;
        if (number > list.size() - 1) {
            number = 0;
        }
        return new RequestVO().setTouser(openId).setData(data).setTemplate_id(msgTempId);
    }

    /**
     * 获取恋爱天数
     * @return
     */
    private long getLoveDay()  {
        try {
            // 转换成日期
            Date dte = myFormatter.parse(loveDay);
            // 时间转换成毫秒值
            long datetime = dte.getTime();
            // 获取当前日期毫秒值
            long nowDate = new Date().getTime();
            // 差值
            long miss = nowDate - datetime;
            return miss / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            log.error("恋爱天数获取错误，异常信息为：{}",e.getMessage());
            return 0;
        }
    }

    /**
     * 获取token
     * @return
     */
    private AccessTokenDTO getAccessToken() {
        String url = MessageFormat.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}", appid, secret);
        return restTemplate.getForObject(url, AccessTokenDTO.class);
    }

    /**
     * 获取天气信息
     * @return
     */
    private WeatherDTO getWeather() {
        String url = "https://v0.yiketianqi.com/free/day?appid=44959372&appsecret=TbU1YpwM&unescape=1&city=商丘";
        return restTemplate.getForObject(url, WeatherDTO.class);
    }

    /**
     *  计算距离生日还有多少天
     * @param birthday：生日日期
     */
    public int getBirthDay(String birthday) {
        int days = 0;
        try {
            Calendar cToday = Calendar.getInstance(); // 存今天
            Calendar cBirth = Calendar.getInstance(); // 存生日

            Date birthdate;
            if (isChineseDate){
                List<Integer> nyr = Arrays.stream(birthday.split("-"))
                        .map(Integer::valueOf).collect(Collectors.toList());
                int year = cToday.get(Calendar.YEAR);
                //传入今年阴历生日获取洋历日期
                ChineseDate chineseDate = new ChineseDate(year,nyr.get(1),nyr.get(2));
                birthdate = chineseDate.getGregorianDate();
                System.out.println("birthdate = " + birthdate);
            }else {
                birthdate = myFormatter.parse(birthday);
            }

            cBirth.setTime(birthdate); // 设置生日
            cBirth.set(Calendar.YEAR, cToday.get(Calendar.YEAR)); // 修改为本年
            if (cBirth.get(Calendar.DAY_OF_YEAR) < cToday.get(Calendar.DAY_OF_YEAR)) {
                // 生日已经过了，要算明年的了
                days = cToday.getActualMaximum(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
                days += cBirth.get(Calendar.DAY_OF_YEAR);
            } else {
                // 生日还没过
                days = cBirth.get(Calendar.DAY_OF_YEAR) - cToday.get(Calendar.DAY_OF_YEAR);
            }
        } catch (ParseException e) {
            log.error("生日天数获取错误，异常信息为：{}",e.getMessage());
        }
        return days;
    }

}
