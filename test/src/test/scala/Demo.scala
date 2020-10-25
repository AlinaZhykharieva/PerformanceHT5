
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class Demo extends Simulation {

	val httpProtocol = http
		.baseUrl("https://demo.nopcommerce.com")

		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")

	val feeder = csv("search.csv").random

	val headers_0 = Map(
		"Accept" -> "text/html, application/xhtml+xml, image/jxr, */*",
		"Accept-Encoding" -> "gzip, deflate",
		"Accept-Language" -> "ru,en-US;q=0.8,en;q=0.5,uk;q=0.3")


	val headers_17 = Map(
		"Accept" -> "*/*",
		"Accept-Encoding" -> "gzip, deflate",
		"Accept-Language" -> "ru,en-US;q=0.8,en;q=0.5,uk;q=0.3",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_32 = Map(
		"Accept" -> "*/*",
		"Accept-Encoding" -> "gzip, deflate",
		"Accept-Language" -> "ru,en-US;q=0.8,en;q=0.5,uk;q=0.3",
		"Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_34 = Map(
		"Client-Request-Id" -> "{F9800917-3978-451D-A972-31270949B877}",
		"Content-Type" -> "text/xml",
		"Pragma" -> "no-cache",
		"Proxy-Connection" -> "Keep-Alive",
		"User-Agent" -> "Microsoft Office/16.0 (Windows NT 10.0; Microsoft Outlook 16.0.13231; Pro)")

    val uri2 = "http://autodiscover.epam.com/autodiscover/autodiscover.xml"

	val scn = scenario("RecordedSimulation")
		// Open main page
		.exec(http("Open main page")
			.get("/")
			.check(regex("<title>(.+?)</title>").is("nopCommerce demo store"))
			.check(substring("demo.nopcommerce.com"))
		.headers(headers_0))
		.pause(1)
		// Search random
		.feed(feeder)
		.exec(http("Search")
			.get("/?f=${searchCriterion}")
			.check(css("a:contains('${searchComputerName}')", "href").saveAs("notebooksURL")))
		.pause(1)
		.exec(http("Get PDP")
			.get("${notebooksURL}")
		.check(css(" input[class=\"button-1 add-to-cart-button\"]", "data-productid").saveAs("productNumber")))
		.pause(1)
			// Add product from product detail page
		.exec(http("Add product from product detail page")
			.post("/addproducttocart/details/${productNumber}/1")
			.check(status.not(500))
			.headers(headers_32)
			.formParam("addtocart_${productNumber}.EnteredQuantity", "2")
			.formParam("__RequestVerificationToken", "CfDJ8NJzpPdWJDZGtf_4GVVpZ2lTmab6NpGA9EGIK5dkT01QzcG6hJGRFtVegC00Z5CQhViBJ8Of6E9QUTkF0nouBJU82XKosbeXoG2VYqaIrU8yWGLaAFfb1CIF8VWEXcRytPOyHw2jUyeKpyXIhu4x6cY"))
		.pause(2)
		// Get cart
		.exec(http("Get cart")
			.get("/cart")
			.check(status.not(400))
			.headers(headers_0)
			.formParam("itemquantity11215", "2")
			.formParam("CountryId", "0")
			.formParam("StateProvinceId", "0")
			.formParam("ZipPostalCode", "")
			.formParam("checkout_attribute_1", "1")
			.formParam("discountcouponcode", "")
			.formParam("giftcardcouponcode", "")
			.formParam("__RequestVerificationToken", "CfDJ8NJzpPdWJDZGtf_4GVVpZ2njgUz47rtzVpN3_WL7KgBzYErFZhP3mogtoLoOgoFRkq4LA_Ybx-KqI9D-SLZe2NRnLGRUL0i3QMFgCG-akjmK4gjcFoLpn3e-mhp-KgnegWsHS9-LzvdcPy4NeqWVyJo"))

	setUp(scn.inject(atOnceUsers(1))).assertions(global.successfulRequests.percent.gt(95)).protocols(httpProtocol)
}