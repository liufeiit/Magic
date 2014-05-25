package miao59;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com._59miao.api.ApiException;
import com._59miao.api.Default59miaoClient;
import com._59miao.api.request._59miaoADSGetRequest;
import com._59miao.api.request._59miaoCouponGetRequest;
import com._59miao.api.request._59miaoCouponItemGetRequest;
import com._59miao.api.request._59miaoCouponListGetRequest;
import com._59miao.api.request._59miaoItemcatsGetRequest;
import com._59miao.api.request._59miaoItemsGetRequest;
import com._59miao.api.request._59miaoItemsSearchRequest;
import com._59miao.api.request._59miaoOrdersReportGetRequest;
import com._59miao.api.request._59miaoPictorialsGetRequest;
import com._59miao.api.request._59miaoPictorialsListGetRequest;
import com._59miao.api.request._59miaoPromocatsGetRequest;
import com._59miao.api.request._59miaoPromosGetRequest;
import com._59miao.api.request._59miaoPromosListGetRequest;
import com._59miao.api.request._59miaoShopcatsGetRequest;
import com._59miao.api.request._59miaoShopsGetRequest;
import com._59miao.api.request._59miaoShopsListGetRequest;
import com._59miao.api.response._59miaoADSGetResponse;
import com._59miao.api.response._59miaoCouponGetResponse;
import com._59miao.api.response._59miaoCouponItemGetResponse;
import com._59miao.api.response._59miaoCouponListGetResponse;
import com._59miao.api.response._59miaoItemcatsGetResponse;
import com._59miao.api.response._59miaoItemsGetResponse;
import com._59miao.api.response._59miaoItemsSearchResponse;
import com._59miao.api.response._59miaoOrdersReportGetResponse;
import com._59miao.api.response._59miaoPictorialsGetResponse;
import com._59miao.api.response._59miaoPictorialsListGetResponse;
import com._59miao.api.response._59miaoPromocatsGetResponse;
import com._59miao.api.response._59miaoPromosGetResponse;
import com._59miao.api.response._59miaoPromosListGetResponse;
import com._59miao.api.response._59miaoShopcatsGetResponse;
import com._59miao.api.response._59miaoShopsGetResponse;
import com._59miao.api.response._59miaoShopsListGetResponse;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年5月23日 下午1:45:20
 */
public class MiaoClient {

	public static void main(String[] args) {
		String strShowApiDataSource = "";
//		Default59miaoClient client = new Default59miaoClient(
//				Constants.PRODUCT_CONTAINER_URL, "1036848",
//				"f2e7dea241045a69839573cfbd5b54d5", "xml");
		
		Default59miaoClient client = new Default59miaoClient(
				"http://api.59miao.com/Router/Rest", "1036848",
				"f2e7dea241045a69839573cfbd5b54d5", "xml");
		
		String strRequestType = "59miao.shops.get";
		//商品类
		if (strRequestType.equals("59miao.items.search")) {
			_59miaoItemsSearchRequest req = new _59miaoItemsSearchRequest();
			req.setFields("iid,click_url,seller_url,title,sid,seller_name,cid,desc,pic_url,price,cash_ondelivery,freeshipment,installment,has_invoice,modified,price_reduction,price_decreases,original_price");
			req.setKeyword("女装");
			try {
				_59miaoItemsSearchResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}

		} else if (strRequestType.equals("59miao.items.get")) {
			_59miaoItemsGetRequest req = new _59miaoItemsGetRequest();
			req.setFields("iid,click_url,seller_url,title,sid,seller_name,cid,desc,pic_url,price,cash_ondelivery,freeshipment,installment,has_invoice,modified,price_reduction,price_decreases,original_price");
			req.setIids("31710424");
			try {
				_59miaoItemsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.itemcats.get")) {
			_59miaoItemcatsGetRequest req = new _59miaoItemcatsGetRequest();
			req.setFields("cid,sid,parent_cid,name,is_parent,status");
			req.setParentCid((long) 0);
			try {
				_59miaoItemcatsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		//商家类
		else if (strRequestType.equals("59miao.shopcats.get")) {

			_59miaoShopcatsGetRequest req = new _59miaoShopcatsGetRequest();
			req.setFields("cid,name,count,status");
			req.setCid((long) 19);
			try {
				_59miaoShopcatsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.shops.list.get")) {

			_59miaoShopsListGetRequest req = new _59miaoShopsListGetRequest();
			req.setFields("sid,name,desc,shop_cat,logo,created,modified,click_url,cashback,payment,shipment,shipment_fee,cash_ondelivery,freeshipment,installment,has_invoice,status,istaobao,popular");

			try {
				_59miaoShopsListGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.shops.get")) {

			_59miaoShopsGetRequest req = new _59miaoShopsGetRequest();
			req.setFields("sid,name,desc,shop_cat,logo,created,modified,click_url,cashback,payment,shipment,shipment_fee,cash_ondelivery,freeshipment,installment,has_invoice,status,istaobao,popular");
			req.setSids("1043");
			try {
				_59miaoShopsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.ads.get")) {
			_59miaoADSGetRequest req = new _59miaoADSGetRequest();
			req.setFields("click_url,pic_size,pic_url,ad_id,sid,title");
			req.setPicSize("300x250");
			req.setSids("1001,1002,1006,1052,1087,1078");
			try {
				_59miaoADSGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		//促销类
		else if (strRequestType.equals("59miao.promocats.get")) {
			_59miaoPromocatsGetRequest req = new _59miaoPromocatsGetRequest();
			req.setFields("cid,parent_cid,name");
			req.setParentCid((long) 0);
			try {
				_59miaoPromocatsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.promos.list.get")) {
			_59miaoPromosListGetRequest req = new _59miaoPromosListGetRequest();
			req.setFields("pid,title,click_url,start_time,end_time,content,sid,seller_name,seller_url");
			req.setCids("9,32");
			try {
				_59miaoPromosListGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.promos.get")) {
			_59miaoPromosGetRequest req = new _59miaoPromosGetRequest();
			req.setFields("pid,title,click_url,start_time,end_time,content,sid,seller_name,seller_url");
			req.setPids("4882");
			try {
				_59miaoPromosGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		//画报类
		else if (strRequestType.equals("59miao.pictorials.list.get")) {
			_59miaoPictorialsListGetRequest req = new _59miaoPictorialsListGetRequest();
			req.setFields("pictorial_id,title,content,desc,pic_url");
			try {
				_59miaoPictorialsListGetResponse rsp = client
						.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.pictorials.get")) {
			_59miaoPictorialsGetRequest req = new _59miaoPictorialsGetRequest();
			req.setFields("title,content,desc,pic_url,pictorial_id");
			req.setPictorialIds("25,24,23");
			try {
				_59miaoPictorialsGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		//优惠卷
		else if (strRequestType.equals("59miao.coupon.list.get")) {
			_59miaoCouponListGetRequest req = new _59miaoCouponListGetRequest();
			req.setFields("sid,title,pid,click_url,seller_url,seller_name,start_time,end_time,seller_logo,pic_url_1,pic_url_2,pic_url_3");
			req.setSids("1001,1052,1087");
			try {
				_59miaoCouponListGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.coupon.get")) {
			_59miaoCouponGetRequest req = new _59miaoCouponGetRequest();
			req.setFields("coupon_id,title,sid,seller_name,seller_logo ,seller_url,click_url,start_time,end_time,desc,item_count,limit,reduce");
			req.setCouponId((long) 30);
			try {
				_59miaoCouponGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else if (strRequestType.equals("59miao.coupon.item.get")) {
			_59miaoCouponItemGetRequest req = new _59miaoCouponItemGetRequest();
			req.setFields("coupon_key,coupon_value");
			req.setCouponId((long) 11);
			try {
				_59miaoCouponItemGetResponse rsp = client.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}

		//功能类
		else if (strRequestType.equals("59miao.orders.report.get")) {
			_59miaoOrdersReportGetRequest req = new _59miaoOrdersReportGetRequest();
			req.setFields("user_id,seller_id,seller_name,order_id,app_key,order_amount,commission,created,ip,order_code,status,outer_code");
			req.setTimestamp((long) 20120922);
			try {
				_59miaoOrdersReportGetResponse rsp = client
						.execute(req);
				strShowApiDataSource = rsp.getBody();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		try {
			System.out.println(URLDecoder.decode(URLEncoder.encode(strShowApiDataSource, "UTF-8"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
