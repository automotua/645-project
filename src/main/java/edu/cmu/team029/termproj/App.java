package edu.cmu.team029.termproj;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;

/**
 * Hello world!
 */
public class App {
	// Initialize parameters
	private static Connection qConnect = null;

	private static final String DB_ELB_HOST = "";
	private static final String DB_NAME = "team645db1";
	private static final String BIZ_TABLE_NAME = "mybusiness";
	private static final String USER_TABLE_NAME = "myuser";
	private static final String REVIEW_TABLE_NAME = "myreview";
	private static final String DB_USERNAME = "team645";
	private static final String DB_PASSWORD = "645termproject";
	private static final int EARTH_R = 6371;

	public App() {
	}

	public static void main(String[] args) {
		// Mysql connections
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// REAL
			qConnect = DriverManager.getConnection(
					"jdbc:mysql://" + DB_ELB_HOST + ":3306/" + DB_NAME + "?", DB_USERNAME,
					DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Undertow server = Undertow.builder().addHttpListener(80, "0.0.0.0")
				.setHandler(new SimpleHttpHandler()).build();
		server.start();

	}

	public static class SimpleHttpHandler implements HttpHandler {

		public void handleRequest(final HttpServerExchange exchange) {
			try {
				String requestURI = exchange.getRequestURI();
				Map<String, Deque<String>> mp = exchange.getQueryParameters();

				if (requestURI.equals("/index.html")) {
					exchange.getResponseSender().send("Server is up!");
				} else if (requestURI.equals("/q_biz")) {
					handleQBiz(exchange, mp);
				} else if (requestURI.equals("/q_review")) {
					handleQReview(exchange, mp);
				} else {
					exchange.getResponseSender().send(
							"Sorry, this query is not supported yet.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void handleQBiz(HttpServerExchange exchange,
								   Map<String, Deque<String>> mp) {
		if (!mp.isEmpty()) {
			String category = mp.get("category").peek();
			String city = mp.get("city").peek();
			String state = mp.get("state").peek();
			String time = mp.get("time").peek();
			String latStr = mp.get("lat").peek();
			String lngStr = mp.get("lng").peek();
			String dist = mp.get("dist").peek();
			String minStars = mp.get("min_stars").peek();
			String maxPrice = mp.get("max_price").peek();

			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
					"text/plain");
			List<Business> bizList = getBizList(category, city, state, time, minStars, maxPrice);
			if (latStr != null && lngStr != null && dist != null) {
				double lat = Double.parseDouble(latStr);
				double lng = Double.parseDouble(lngStr);
				int d = Integer.parseInt(dist);
				bizList = filterByDistance(bizList, lat, lng, d);
			}
			StringBuffer sb = new StringBuffer();
			for (Business business : bizList) {
				sb.append(business.toString());
				sb.append("\n");
			}
			exchange.getResponseSender().send(sb.toString());
		}
		exchange.getResponseSender().send("Sorry, please give proper parameters.");
	}

	private static List<Business> filterByDistance(List<Business> bizList, double lat, double lng, int d) {
		List<Business> newBizList = new ArrayList<>();
		for (Business business : bizList) {
			double oneBizLat = business.getLatitude();
			double oneBizLng = business.getLongtitude();
			double oneD = getDistanceFromLatLonInKm(oneBizLat, oneBizLng, lat, lng);
			if (oneD < d) {
				newBizList.add(business);
			}
		}
		return newBizList;
	}

	private static double getDistanceFromLatLonInKm(double lat1, double lng1,
													double lat2, double lng2) {
		double dLat = deg2rad(lat2 - lat1);
		double dLng = deg2rad(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad
				(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLng / 2) * Math
				.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = EARTH_R * c;
		return d;
	}

	private static double deg2rad(double deg) {
		return deg * (Math.PI / 180);
	}


	private static List<Business> getBizList(String category, String city, String state,
											 String time, String minStars, String maxPrice) {
		PreparedStatement preparedStatement;
		String query = "";
		query += "select * from " + DB_NAME + "." + BIZ_TABLE_NAME + " where 1=1 ";
		List<Business> bizList = new ArrayList<>();
		if (category != null) {
			query += "and (categories like '%" + category + "%') ";
		}
		if (city != null) {
			query += "and (city = '" + city + "') ";
		}
		if (state != null) {
			query += "and (state = '" + state + "') ";
		}
		if (time != null) {
			query += "and (strcmp('open', '" + time + "') <= 0 and strcmp('close', '" + time + "') >= 0) ";
		}
		if (minStars != null) {
			query += "and (stars >= " + Integer.parseInt(minStars) + ") ";
		}
		if (maxPrice != null) {
			query += "and (price_range <= " + Integer.parseInt(maxPrice) + ")";
		}
		query += ";";
		try {
			preparedStatement = qConnect.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Business biz = createBizFromResult(resultSet);
				bizList.add(biz);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bizList;
	}

	private static Business createBizFromResult(ResultSet resultSet) {
		Business biz = new Business();

		try {
			biz.setBizId(resultSet.getString("business_id"));
			biz.setFullAddress(resultSet.getString("full_address"));
			biz.setLatitude(resultSet.getDouble("latitude"));
			biz.setLongtitude(resultSet.getDouble("longitude"));
			biz.setName(resultSet.getString("name"));
			biz.setPriceRange(resultSet.getInt("price_range"));
			biz.setOpenTime(resultSet.getString("open"));
			biz.setCloseTime(resultSet.getString("close"));
			biz.setCity(resultSet.getString("city"));
			biz.setState(resultSet.getString("state"));
			biz.setCategories(resultSet.getString("categories"));
			biz.setStars(resultSet.getDouble("stars"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return biz;
	}

	private static void handleQReview(final HttpServerExchange exchange,
									  Map<String, Deque<String>> mp) throws SQLException {
		if (!mp.isEmpty()) {
			String bizId = mp.get("biz_id").peek();
			String criteria = mp.get("criteria").peek();

			exchange.getResponseHeaders().put(Headers.CONTENT_TYPE,
					"text/plain");

			List<Review> reviewList = new ArrayList<>();
			PreparedStatement preparedStatement;
			String query = "";
			query += "select u.name as username, r.date as date, r.content as content, r.stars as stars from " +
					DB_NAME + "." + REVIEW_TABLE_NAME + " as r, " + DB_NAME + "." + USER_TABLE_NAME + " as u where r" +
					".business_id = '" + bizId + "' and r.user_id = u.user_id ";
			if (criteria != null) {
				query += "and u.review_count >= 100";
			}
			query += ";";
			try {
				preparedStatement = qConnect.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					Review r = createReviewFromResult(resultSet);
					reviewList.add(r);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			StringBuffer sb = new StringBuffer();
			for (Review r : reviewList) {
				sb.append(r.toString());
				sb.append("\n");
			}
			exchange.getResponseSender().send(sb.toString());
		}
		exchange.getResponseSender().send("Sorry, please give proper parameters.");
	}

	private static Review createReviewFromResult(ResultSet resultSet) {
		Review r = new Review();
		try {
			r.setContent(resultSet.getString("content"));
			r.setDate(resultSet.getString("date"));
			r.setStars(resultSet.getDouble("stars"));
			r.setUserName(resultSet.getString("username"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}


}
