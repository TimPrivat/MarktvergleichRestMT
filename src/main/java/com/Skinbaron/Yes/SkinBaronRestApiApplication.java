package com.Skinbaron.Yes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.util.encoders.Base32;
import org.cryptacular.generator.TOTPGenerator;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SpringBootApplication
public class SkinBaronRestApiApplication {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static void main(String[] args) {
		SpringApplication.run(SkinBaronRestApiApplication.class, args);

		System.out.println("Hallo");
		// String s = toPrettyFormat(j.toJSONString());
		//Test();
		// System.out.println(s);

		ablauf();
		// System.out.println( new
		// SkinBaronRestApiApplication().getClass().getProtectionDomain().getCodeSource().getLocation());

		// syncSkinport();
		// syncSteam();
		// System.out.println(getSteamPrice("Sticker | fox | Cologne 2016"));
		// syncSkinbaron();

		// steampath = "E://SteamMarketData//Steam_2022_10_13-19_16_03.txt";

		// System.out.println(getSteamPrice("AK-47 | Fire Serpent (Field-Tested)"));

		System.out.println("Fertig");
	}

	// Der Filepath zur skinportfile
	static String skinportpath;
	static String skinbaronpath;
	static String steampath;
	static ArrayList<Skin> allSkins;
	private static JSONArray Skinportarr;
	static TreeMap<String, Double> SkinPortMap;
	private static JSONArray Skinbaronarr = null;
	static TreeMap<String, Double> SkinBaronMap;
	static ArrayList<String> hashnames;
	static JSONObject Steamarr;
	static int allThreadshavefinished = 0;
	// static int counter = 0;

	@SuppressWarnings("unchecked")
	public static void ablauf() {

		try {
			System.out.println("Starting Skinport");
			JSONObject Skinport = parseObject(new File(syncSkinport()));
			System.out.println("Starting SkinBaron");
			JSONObject Skinbaron = parseObject(new File(syncSkinbaron()));
			System.out.println("Starting Steam");
			JSONObject Steam = parseObject(new File(syncSteam()));

			Skinportarr = (JSONArray) Skinport.get("alles");
			Skinbaronarr = (JSONArray) Skinbaron.get("map");
			hashnames = getSkinnames();
			Steamarr = (JSONObject) Steam.get("items_list");
			allSkins = new ArrayList<>();
			
			arrangSkinbaronArray();
			arrangSkinportArray();

			// So auf das Array Zugreifen
			// System.out.println(((JSONObject) Skinbaronarr.get(3)).get("lowestPrice"));

			// System.out.println(toPrettyFormat(Steamarr.toJSONString()));

			int Threadcount = 15;

			// SEHR Mächtige schleife
			for (int i = 0; i < Threadcount; i++) {

				new AblaufThread(Threadcount, i).start();

			}

			while (allThreadshavefinished < Threadcount) {

				Thread.sleep(10000);
			}
			// Klon für 2ten Sortingalgorithmus
			ArrayList<Skin> allskinsClone = new ArrayList<>();

			for (int i = 0; i < allSkins.size(); i++) {

				allskinsClone.add(allSkins.get(i));
			}

			allSkins.sort(new Skincomparator());

			File ergebnisfile = new File("E://SteamMarketData//ErgebnisFile_Prozent_" + getDate() + ".txt");
			ergebnisfile.createNewFile();

			JSONArray erg = new JSONArray();

			for (int i = 0; i < allSkins.size(); i++) {

				if (allSkins.get(i).bessererWert != null && allSkins.get(i).MarktplatzProzent != null) {

					Skin skin = allSkins.get(i);

					JSONObject tmp = new JSONObject();
					tmp.put("markethash", skin.markethash);
					tmp.put("SteamPreis", skin.Steampreis);
					tmp.put("SteamPreis nach Steuern", round(skin.SteampreisnachSteuern));
					tmp.put("Marktplatz", skin.MarktplatzProzent);

					if (skin.MarktplatzProzent.equals("SkinBaron")) {

						tmp.put("SkinBaronPreis", round(skin.Skinbaronpreis));
						tmp.put("Differenz", round(skin.SkinBaronPreisdifferenzEuro));
						tmp.put("ProzentDifferenz", round(skin.SkinBaronPreisdifferenzProzent));
					} else {

						tmp.put("SkinPortPreis", round(skin.Skinportpreis));
						tmp.put("Differenz", round(skin.SkinportPreisdifferenzEuro));
						tmp.put("ProzentDifferenz", round(skin.SkinportPreisdifferenzProzent));

					}

					erg.add(tmp);

				}
			}

			JSONObject alles = new JSONObject();
			alles.put("yes", erg);
			String s = alles.toJSONString();
			String ps = toPrettyFormat(s);
			System.out.println(ps);
			write(ps, ergebnisfile);

			allskinsClone.sort(new SkincomperatorEuro());

			File ergebnisfileEuro = new File("E://SteamMarketData//ErgebnisFile_Euro_" + getDate() + ".txt");
			ergebnisfileEuro.createNewFile();

			JSONArray erg2 = new JSONArray();

			for (int i2 = 0; i2 < allskinsClone.size(); i2++) {

				if (allskinsClone.get(i2).bessererWert != null && allskinsClone.get(i2).MarktplatzEuro != null) {

					Skin skin = allskinsClone.get(i2);

					JSONObject tmp = new JSONObject();
					tmp.put("markethash", skin.markethash);
					tmp.put("SteamPreis", skin.Steampreis);
					tmp.put("SteamPreis nach Steuern", round(skin.SteampreisnachSteuern));
					tmp.put("Marktplatz", skin.MarktplatzEuro);

					if (skin.MarktplatzEuro.equals("SkinBaron")) {

						tmp.put("SkinBaronPreis", round(skin.Skinbaronpreis));
						tmp.put("Differenz", round(skin.SkinBaronPreisdifferenzEuro));
						tmp.put("ProzentDifferenz", round(skin.SkinBaronPreisdifferenzProzent));
					} else {

						tmp.put("SkinPortPreis", round(skin.Skinportpreis));
						tmp.put("Differenz", round(skin.SkinportPreisdifferenzEuro));
						tmp.put("ProzentDifferenz", round(skin.SkinportPreisdifferenzProzent));

					}

					erg2.add(tmp);

				}

			}

			JSONObject alles2 = new JSONObject();
			alles2.put("yes", erg2);
			String s2 = alles2.toJSONString();
			String ps2 = toPrettyFormat(s2);
			System.out.println(ps2);
			write(ps2, ergebnisfileEuro);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@SuppressWarnings("deprecation")
	public static String syncSteam() {

		// Für genaue Preisdaten, aber nur 20 Anfragen pro Minute

		/*
		 * RestTemplate restTemplate = new RestTemplate();
		 * 
		 * URI u = null;
		 * 
		 * // String b = "AK-47 | Aquamarine Revenge (Field-Tested)"; String
		 * normalisiert = normalisieren(hashname); String uri =
		 * "https://steamcommunity.com/market/priceoverview/?appid=730&currency=3&market_hash_name="
		 * + normalisiert;
		 * 
		 * try { u = new URI(uri);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * JSONObject s = restTemplate.getForObject(u, JSONObject.class); //
		 * System.out.println(s.toJSONString());
		 * 
		 * return s;
		 */

		// Gibt ganze List zurück
		// Die Daten werden alle 8 Stunden aktualisiert
		// --> nicht 100% aktuell

		RestTemplate r = new RestTemplate();

		JSONObject h = new JSONObject();
		// h.put("currency", "EUR");
		h.put("api_key", "juLCSKqmzVIF2fj6181rINv7F3M");
		h.put("format", "compact");

		// Free API
		// String a = r.postForObject("http://csgobackpack.net/api/GetItemsList/v2/", h,
		// String.class);

		// Kostet 5€ im Monat
		String a = r.getForObject(
				"https://api.steamapis.com/market/items/730?api_key=juLCSKqmzVIF2fj6181rINv7F3M&format=compact",
				String.class, h);
		String s = toPrettyFormat(a);

		File f = new File("E://SteamMarketData//Steam_" + getDate() + ".txt");
		try {
			f.createNewFile();
		} catch (IOException e) { // TODO Auto-generated catch
			e.printStackTrace();
		}
		write(s, f);
		System.out.println(s);
		steampath = f.getAbsolutePath();
		return f.getAbsolutePath();

		// Aller guten dinge sind 3
		// ... nicht wirklich die neuesten preise sind 2 monate alt

		/*
		 * JSONObject allSk = parseObject(new
		 * File("C://Users//timle//Desktop//Steam//AllSkinnames.txt"));
		 * 
		 * JSONArray allSkinnames = (JSONArray) allSk.get("yes");
		 * 
		 * RestTemplate r = new RestTemplate(); HashMap<String, String> h = new
		 * HashMap<>(); h.put("api_key", "b9633511-38a6-496a-898d-0c3415ad98f9");
		 * h.put("app_id", "730"); for (int i = 5; i < allSkinnames.size() ; i++) {
		 * String name = (String) allSkinnames.get(i); String code = getcurrentCode();
		 * h.put("code", code); h.put("market_hash_name", name);
		 * System.out.println(code); JSONObject sr =
		 * r.postForObject("https://bitskins.com/api/v1/get_steam_price_data", h,
		 * JSONObject.class); System.out.println(sr.getClass());
		 * 
		 * HashMap t = (HashMap) sr.get("data"); JSONObject s = new JSONObject(t); //
		 * System.out.println(t.get("app_id")); System.out.println(s); if
		 * (s.get("raw_data") != null) {
		 * 
		 * long maxtime = 0;
		 * 
		 * Object a = s.get("raw_data"); System.out.println(a.getClass());
		 * ArrayList<Map> ar = (ArrayList<Map>) a; JSONArray arr = new JSONArray();
		 * ar.forEach(n -> arr.add(n));
		 * 
		 * for (int o = 0; o < arr.size(); o++) { System.out.println(arr.get(o)); int
		 * num = (int) ((Map) arr.get(o)).get("time"); if (num > maxtime) maxtime = num;
		 * }
		 * 
		 * System.out.println("Maxtime " + maxtime); } System.out.println("updated at:"
		 * + s.get("updated_at")); }
		 */

		// return null;

	}

	@SuppressWarnings("unchecked")
	public static String syncSkinport() {
		String date = getDate();
		String skinport = "E://SteamMarketData//Skinport_" + date + ".txt";
		System.out.println(skinport);

		File f = new File(skinport);
		try {
			f.createNewFile();
			System.out.println("New File created");
		} catch (IOException e) {
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		JSONArray j = restTemplate.getForObject("https://api.skinport.com/v1/items", JSONArray.class);
		System.out.println(j.size());
		JSONObject alles = new JSONObject();
		alles.put("alles", j);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String s = gson.toJson(alles);
		s = s.replaceAll("min_price", "min_price_skinport");
		s = s.replaceAll("max_price", "max_price_skinport");

		// System.out.println(s);

		write(s, f);
		skinportpath = f.getAbsolutePath();
		return f.getAbsolutePath();

	}

	@SuppressWarnings("deprecation")
	public static String syncSkinbaron() {

		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.MINUTES).build();

		MediaType mediaType = MediaType.parse("application/json");

		JSONObject param = new JSONObject();
		param.put("apikey", "578576-75041f12-bd73-469b-93dd-7088416f6e5d");
		param.put("appId", "730");
		// param.put("size", "100");

		System.out.println(param.toJSONString());

		RequestBody body = RequestBody.create(mediaType, param.toJSONString());

		Request request = new Request.Builder().url("https://api.skinbaron.de/GetExtendedPriceList")
				.method("POST", body).addHeader("Content-Type", "application/json")
				.addHeader("x-requested-with", "XMLHttpRequest").build();

		// Geht erst weiter, wenn er eine erfolgreiche Antwort bekommen hat
		Call call = null;
		Response response = null;
		request = new Request.Builder().url("https://api.skinbaron.de/GetExtendedPriceList").method("POST", body)
				.addHeader("Content-Type", "application/json").addHeader("x-requested-with", "XMLHttpRequest").build();

		boolean notnull = false;
		int Skinbarontrys = 0;

		try {
			while (!notnull) {

				System.out.println("Skinbaronfetchtrys:" + ++Skinbarontrys);
				call = client.newCall(request);

				try {
					response = call.execute();
					Thread.sleep(1000);
					notnull = true;
				} catch (Exception e) {
				}

				Thread.sleep(3000);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = null;
		try {
			s = response.body().string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String pretty = toPrettyFormat(json.toJSONString());
		System.out.println(pretty);

		File f = new File("E://SteamMarketData//SkinBaron_" + getDate() + ".txt");
		skinbaronpath = f.getAbsolutePath();

		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		write(pretty, f);

		return f.getAbsolutePath();

	}

	public static void write(String s, File f) {

		FileWriter myWriter;
		try {
			myWriter = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(myWriter);
			bw.write(s);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);

		return prettyJson;
	}

	public static String normalisieren(String s) {

		s = s.replaceAll(" ", "%20");
		s = s.replaceAll("\\|", "%7C");

		URLEncoder.encode(s, StandardCharsets.UTF_8);

		return s;

	}

	public static JSONArray parseArray(File f) {

		JSONParser parser = new JSONParser();

		Object obj = null;

		try {
			obj = parser.parse(new FileReader(f));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray wert = (JSONArray) obj;

		return wert;

	}

	public static JSONObject parseObject(File f) {

		JSONParser parser = new JSONParser();

		Object obj = null;

		try {
			obj = parser.parse(new FileReader(f));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject wert = (JSONObject) obj;

		return wert;

	}

	static Double getSteamPrice(String markethashname) {

		File f = new File(steampath);

//SteamApi Kostet 5€ im Monat

		JSONObject ganzefile = parseObject(f);
		Number tmp = (Number) ganzefile.get(markethashname);
		Double price;
		try {
			price = (Double) tmp;

		} catch (Exception e) {

			Long a = (Long) tmp;
			price = a * 1.0;
		}
		return price;

// GratisVersion mit CSGOBackpack: 
//		Scanner sc = null;
//
//		try {
//			sc = new Scanner(f);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		while (sc.hasNextLine()) {
//			String line = sc.nextLine();
//
//			if (line.contains("\"name\":")) {
//
//				line = line.replaceAll("\"name\":", "");
//				line = line.substring(0, line.length() - 1);
//				line = line.trim();
//				line = line.substring(1, line.length() - 1);
//
//				if (line.equals(markethashname)) {
//
//					boolean fertig = false;
//
//					while (fertig == false || !line.contains("\"median\": ")) {
//
//						line = sc.nextLine();
//
//						if (line.contains(" \"7_days\":")) {
//
//							line = sc.nextLine();
//							line = sc.nextLine();
//
//							line = line.replaceAll(" ", "");
//							line = line.replaceAll("\"", "");
//							line = line.replaceAll(":", "");
//							line = line.replaceAll(",", "");
//							line = line.replaceAll("median", "");
//
//							Double ergebnis = Double.parseDouble(line);
//
//							if (ergebnis == 0) {
//
//							} else {
//
//								return ergebnis;
//
//							}
//
//						} else if (line.contains(" \"30_days\":")) {
//
//							fertig = true;
//
//						}
//
//					}
//
//					line = line.replaceAll(" ", "");
//					line = line.replaceAll("\"", "");
//					line = line.replaceAll(":", "");
//					line = line.replaceAll(",", "");
//					line = line.replaceAll("median", "");
//
//					Double ergebnis = Double.parseDouble(line);
//
//					// System.out.println(ergebnis);
//
//					return ergebnis;
//				}
//
//			}
//		}
//
//		return null;

	}

	static ArrayList<String> getSkinnames() {

		File f = new File(steampath);
		System.out.println(steampath);

//Bezahlte Version mit SteamAPI

		JSONObject steam = parseObject(f);

		return new ArrayList<String>(steam.keySet());

// KostenLose Version mit CSGOBackpack
//		File f = new File(steampath);
//		Scanner sc = null;
//		ArrayList<String> erg = new ArrayList<>();
//		try {
//			sc = new Scanner(f);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String line;
//		while (sc.hasNextLine()) {
//			line = sc.nextLine();
//
//			if (line.contains("\"name\":")) {
//
//				line = line.replaceAll("\\u0026#39", "\\u0027");
//				line = line.replaceAll("\"name\":", "");
//				line = line.substring(0, line.length() - 1);
//				line = line.replaceAll("\"", "");
//
//				line = line.substring(1);
//				line = line.trim();
//				System.out.println(line);
//				erg.add(line);
//			}
//
//		}

//		return erg;

	}

	static String getDate() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
		Date date = new Date(System.currentTimeMillis());
		String ergebnis = (formatter.format(date));

		System.out.println("Uhrzeit: " + ergebnis);
		return ergebnis;
	}

	public static Double round(Double value) {

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String getcurrentCode() {
		// Generiert BitskinsCode

		String secret = "CAQRMOBGQBL36EDN";

		TOTPGenerator gen = new TOTPGenerator();
		int co = gen.generate(Base32.decode(secret));

		String code = Integer.toString(co);
		return code;

	}
	//Minimiert das SkinBaronarray und sortiert es nach Keynames
	private static void arrangSkinbaronArray() {

		HashMap<String, Double> SkinbaronTempMap = new HashMap<>();

		for (int o = 0; o < Skinbaronarr.size(); o++) {

			JSONObject sb = (JSONObject) Skinbaronarr.get(o);
			String sbname = (String) sb.get("marketHashName");

			String asString = sb.toJSONString();

			Gson gson = new Gson();
			SkinBaronSkin s = gson.fromJson(sb.toString(), SkinBaronSkin.class);

			String FN = "(Factory New)";
			String MW = "(Minimal Wear)";
			String FT = "(Field-Tested)";
			String WW = "(Minimal Wear)";
			String BS = "(Battle-Scarred)";

			Double Skinbaronprice = null;

			// Es gibt sonderfälle, wo skins in der Skinbaronfile stehen, aber weder ST
			// noch Souv sind, aber als souvenier gezählt werden und den preis von billigen
			// Items verfälschen

			/*
			 * if ((sbname.contains(FN) || sbname.contains(MW) || sbname.contains(FT) ||
			 * sbname.contains(WW) || sbname.contains(BS)) && (s.souvenir == null &&
			 * s.statTrak == null)) { continue;
			 * 
			 * }
			 */

			if (s.souvenir != null) {
				if (!s.souvenir && sbname.contains("Souvenir")) {
					sbname = sbname.replaceAll("Souvenir ", "");
				}
				if (s.souvenir && !sbname.contains("Souvenir")) {
					sbname = "Souvenir " + sbname;
				}
			}
			if (s.statTrak != null) {

				if (!s.statTrak && sbname.contains("StatTrak™")) {
					sbname = sbname.replaceAll("StatTrak™ ", "");
				}
				if (s.statTrak && !sbname.contains("StatTrak™")) {
					sbname = "StatTrak™ " + sbname;
				}
			}

			try {

				boolean wear = false;

				try {

					Double w = (Double) sb.get("minWear");

					if (w != null || w != 0.0) {

						wear = true;

					}

				} catch (Exception e) {

				}

				if (sb.get("statTrak") == null && sb.get("souvenir") == null && (wear))
					continue;

				Skinbaronprice = (Double) sb.get("lowestPrice");

			} catch (ClassCastException e) {
				Long tmp = (Long) sb.get("lowestPrice");
				Skinbaronprice = tmp * 1.0;

			}

			SkinbaronTempMap.put(sbname, Skinbaronprice);

		}

		Map<String, Double> FertigesSkinbaronArray = new TreeMap<>(SkinbaronTempMap);

		/*File f = new File("E://SteamMarketData//SkinBaron_Clean_" + getDate() + ".txt");

		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String s = FertigesSkinbaronArray.toString();
		write(s, f);
*/
		SkinBaronMap = (TreeMap<String, Double>) FertigesSkinbaronArray;
	}

	//Minimiert das Skinportarray und sortiert es nach Keynames
	private static void arrangSkinportArray() {

		HashMap<String, Double> SkinportTempMap = new HashMap<>();

		for (int i = 0; i < Skinportarr.size(); i++) {

			JSONObject sp = (JSONObject) SkinBaronRestApiApplication.Skinportarr.get(i);

			String spName = (String) sp.get("market_hash_name");
			Double SkinportPreis = null;
			try {

				SkinportPreis = (Double) sp.get("min_price_skinport");

			} catch (ClassCastException e) {

				Long tmp = (Long) sp.get("min_price_skinport");
				SkinportPreis = tmp * 1.0;

			}
			SkinportTempMap.put(spName, SkinportPreis);
		}
		
		Map<String, Double> FertigesSkinportArray = new TreeMap<>(SkinportTempMap);

		/*File f = new File("E://SteamMarketData//SkinPort_Clean_" + getDate() + ".txt");

		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String s = FertigesSkinportArray.toString();
		write(s, f);
*/
		SkinPortMap = (TreeMap<String, Double>) FertigesSkinportArray;

	}

	static void Test() {

		JSONObject Skinbaron = parseObject(new File("E:\\SteamMarketData\\Skinport_2023_04_08-13_59_55.txt"));

		Skinportarr = (JSONArray) Skinbaron.get("alles");

		new SkinBaronRestApiApplication().arrangSkinportArray();
		// syncSkinbaron();

		/*
		 * steampath = "E://SteamMarketData//Steam_2022_08_10-20_47_49.txt";
		 * ArrayList<String> skinnames = getSkinnames();
		 * 
		 * JSONArray ar = new JSONArray(); for (String s : skinnames) { ar.add(s);
		 * 
		 * } JSONObject yes = new JSONObject(); yes.put("yes", ar);
		 * write(toPrettyFormat(yes.toJSONString()), new
		 * File("C://Users//timle//Desktop//Steam//AllSkinnames.txt"));
		 */

		/*
		 * steampath = "E://SteamMarketData//Steam_2022_10_13-23_47_46.txt"; String name
		 * = "Souvenir G3SG1 | VariCamo (Well-Worn)"; JSONObject Skinbaron =
		 * parseObject(new File(syncSkinbaron())); Skinbaronarr = (JSONArray)
		 * Skinbaron.get("map"); hashnames = getSkinnames();
		 * 
		 * Double Skinbaronprice = null;
		 * 
		 * for (int o = 0; o < SkinBaronRestApiApplication.Skinbaronarr.size(); o++) {
		 * 
		 * JSONObject sb = (JSONObject) SkinBaronRestApiApplication.Skinbaronarr.get(o);
		 * String sbname = (String) sb.get("marketHashName");
		 * 
		 * String asString = sb.toJSONString();
		 * 
		 * Gson gson = new Gson(); SkinBaronSkin s = gson.fromJson(sb.toString(),
		 * SkinBaronSkin.class);
		 * 
		 * String FN = "(Factory New)"; String MW = "(Minimal Wear)"; String FT =
		 * "(Field-Tested)"; String WW = "(Minimal Wear)"; String BS =
		 * "(Battle-Scarred)";
		 * 
		 * // Es gibt sonderfälle, wo skins in der Skinbaronfile stehen, aber weder ST
		 * // noch Souv sind, aber als souvenier gezählt werden und den preis von
		 * billigen // Items verfälschen
		 * 
		 * /* if ((sbname.contains(FN) || sbname.contains(MW) || sbname.contains(FT) ||
		 * sbname.contains(WW) || sbname.contains(BS)) && (s.souvenir == null &&
		 * s.statTrak == null)) { continue;
		 * 
		 * }
		 */

		/*
		 * if (s.souvenir != null) { if (!s.souvenir && sbname.contains("Souvenir")) {
		 * sbname = sbname.replaceAll("Souvenir ", ""); } if (s.souvenir &&
		 * !sbname.contains("Souvenir")) { sbname = "Souvenir " + sbname; } } if
		 * (s.statTrak != null) {
		 * 
		 * if (!s.statTrak && sbname.contains("StatTrak™")) { sbname =
		 * sbname.replaceAll("StatTrak™ ", ""); } if (s.statTrak &&
		 * !sbname.contains("StatTrak™")) { sbname = "StatTrak™ " + sbname; } }
		 * 
		 * if (sbname.equals(name)) { try {
		 * 
		 * boolean wear = false;
		 * 
		 * try {
		 * 
		 * Double w = (Double) sb.get("minWear");
		 * 
		 * if (w != null || w != 0.0) {
		 * 
		 * wear = true;
		 * 
		 * }
		 * 
		 * } catch (Exception e) {
		 * 
		 * }
		 * 
		 * if (sb.get("statTrak") == null && sb.get("souvenir") == null && (wear))
		 * continue;
		 * 
		 * Skinbaronprice = (Double) sb.get("lowestPrice");
		 * 
		 * } catch (ClassCastException e) { Long tmp = (Long) sb.get("lowestPrice");
		 * Skinbaronprice = tmp * 1.0;
		 * 
		 * }
		 * 
		 * break; }
		 * 
		 * } System.out.println("SkinbaronPreis: "+ Skinbaronprice);
		 */

	}

}
