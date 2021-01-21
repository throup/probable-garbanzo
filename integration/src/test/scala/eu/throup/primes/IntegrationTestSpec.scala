package eu.throup
package primes

import io.restassured.RestAssured.when
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

class IntegrationTestSpec extends AnyFreeSpec {
  "Integration test" - {
    val serviceThread: Thread = serviceApp
    val proxyThread: Thread = proxyApp

    "success scenarios" - {
      "2 => 2." in {
        when().get("/prime/2")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be ("2.")
      }

      "5 => 2,3,5." in {
        when().get("/prime/5")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be ("2,3,5.")
      }

      "20 => 2,3,5,7,11,13,17,19." in {
        when().get("/prime/20")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be ("2,3,5,7,11,13,17,19.")
      }

      "1234 => 2,...,1231." in {
        when().get("/prime/1234")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be ("2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809,811,821,823,827,829,839,853,857,859,863,877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997,1009,1013,1019,1021,1031,1033,1039,1049,1051,1061,1063,1069,1087,1091,1093,1097,1103,1109,1117,1123,1129,1151,1153,1163,1171,1181,1187,1193,1201,1213,1217,1223,1229,1231.")
      }
    }

    "unusual scenarios" - {
      "0 => ." in {
        when().get("/prime/0")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be (".")
      }

      "-1 => ." in {
        when().get("/prime/-1")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be (".")
      }

      "+15 => 2,3,5,7,11,13." in {
        when().get("/prime/+15")
          .then()
          .statusCode(200)
          .extract().body()
          .asString() should be ("2,3,5,7,11,13.")
      }
    }

    "Not found scenarios" -{
      "/prime/notANumber => Not found" in {
        when().get("/prime/notANumber")
          .then()
          .statusCode(404)
          .extract().body()
          .asString() should be ("Not found")
      }

      "/prime/5.4e3 => Not found" in {
        when().get("/prime/5.4e3")
          .then()
          .statusCode(404)
          .extract().body()
          .asString() should be ("Not found")
      }

      "/some/other/path => Not found" in {
        when().get("/some/other/path")
          .then()
          .statusCode(404)
          .extract().body()
          .asString() should be ("Not found")
      }
    }
  }

  private def serviceApp = {
    val service = prime_number_server.Main
    val serviceThread = new Thread {
      override def run {
        service.main(Array.empty)
      }
    }
    serviceThread.start
    serviceThread
  }

  private def proxyApp = {
    val proxy = proxy_service.Main
    val proxyThread = new Thread {
      override def run {
        proxy.main(Array.empty)
      }
    }
    proxyThread.start
    proxyThread
  }
}
