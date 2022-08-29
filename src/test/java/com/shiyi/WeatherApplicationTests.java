package com.shiyi;

import com.google.gson.Gson;
import com.shiyi.dto.AccessTokenDTO;
import com.shiyi.dto.WeatherDTO;
import com.shiyi.vo.DataVO;
import com.shiyi.vo.PropertyVO;
import com.shiyi.vo.RequestVO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class WeatherApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(WeatherApplicationTests.class);

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
				logger.error("读取文件内容失败，异常信息为：{}",e.getMessage());
			}
			list.add(content);
		}
	}

	@Test
	void contextLoads() {
		RequestVO requestVO = buildData();
		AccessTokenDTO accessToken = getAccessToken();

		String json = gson.toJson(requestVO);
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		HttpEntity formEntity = new HttpEntity(json, headers);
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken.getAccess_token();
		Object result = restTemplate.postForObject(url, formEntity, Object.class);
		assert result != null;
		System.out.println("发送成功,结果为：" + result);
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
				.setBirthday(PropertyVO.init(getBirthDay("1998-03-11"),"#0033FF"));
		number++;
		if (number > list.size() - 1) {
			number = 0;
		}
		return new RequestVO().setTouser(openId).setData(data);
	}

	/**
	 * 获取恋爱天数
	 * @return
	 */
	private long getLoveDay()  {
		try {
			// 转换成日期
			Date dte = myFormatter.parse("2022-07-25");
			// 时间转换成毫秒值
			long datetime = dte.getTime();
			// 获取当前日期毫秒值
			long nowDate = new Date().getTime();
			// 差值
			long miss = nowDate - datetime;
			return miss / (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
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
		String url = "https://www.yiketianqi.com/free/day?appid=44959372&appsecret=TbU1YpwM&unescape=1&city=长沙";
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
			cBirth.setTime(myFormatter.parse(birthday)); // 设置生日
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
		}
		return days;
	}
}
