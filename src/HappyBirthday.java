import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class HappyBirthday {

	private static final String GUBUN = "_";
	private static final String path = "D:\\SAMPLE\\";
	Gson gson = new Gson();
	Map<String, Map<String, Object>> allMap = new HashMap<String, Map<String, Object>>();

	public static void main(String[] args) {
		HappyBirthday hb = new HappyBirthday();
		hb.run(path+"csv 파일 경로", "euc-kr");
	}

	private void hashMapTemp(Map<String, String> process, String json, ArrayList<String> numberList) {
		try {
			process = (Map<String, String>) gson.fromJson(json, process.getClass());
			for (String key : process.keySet()) {
				if (!numberList.contains(key)) {
					numberList.add(key);
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(json);
		}
	}

	private Map<String, Integer> hashMapProcess(String json, ArrayList<String> numberList) {
		try {
			json = json.replaceAll("\\\"", "");
			if ("\\N".equals(json)) {
				return null;
			}
			Map<String, Integer> process = new HashMap<>();
			process = (Map<String, Integer>) gson.fromJson(json, process.getClass());
			for (String key : process.keySet()) {
				if (!numberList.contains(key)) {
					numberList.add(key);
				}
			}
			return process;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 중복되는 요청이 없음.
	private void hashMapProcessSum(Map<String, Integer> processOrigin, Map<String, Integer> processNew) {
		for (String key : processNew.keySet()) {
			if (processOrigin.get(key) != null) {
				System.out.println("not null" + processOrigin.get(key) + " temp - " +  processNew.get(key));
				processOrigin.put(key, processOrigin.get(key) + processNew.get(key));
			} else {
				System.out.println("not null" + processOrigin.get(key) + " temp - " +  processNew.get(key));
				processOrigin.put(key, processNew.get(key));
			}
		}
	}

	private void run(String path, String encoding) {
		BufferedReader br = null;
		String line;
		String cvsSplitBy = "\t";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(cvsSplitBy);
				try {
					// sdate site_name userid site_code platform product ad_gubun dcol_view_freqs
					// dcol_click_freqs dcol_real_conv_freqs dcol_direct_conv_freqs
					// dcol_indirect_conv_freqs
					String key = fields[0] + GUBUN + fields[1] + GUBUN + fields[2] + GUBUN + fields[3] + GUBUN + fields[4] + GUBUN + fields[5] + GUBUN + fields[6];
					Map<String, Object> temp = allMap.get(key);

					if (allMap.get(key) == null) {
						temp = new HashMap<String, Object>();
					}

					ArrayList<String> numberList = (ArrayList<String>) temp.get("number");

					if (temp.get("number") == null) {
						numberList = new ArrayList<>();
						temp.put("number", numberList);
					}

					Map<String, Integer> dcol_view_freqs = hashMapProcess(fields[7], numberList);

					if (temp.get("dcol_view_freqs") == null) {
						temp.put("dcol_view_freqs", dcol_view_freqs);
					} else {
						hashMapProcessSum((Map<String, Integer>) temp.get("dcol_view_freqs"), dcol_view_freqs);
					}

					Map<String, Integer> dcol_click_freqs = hashMapProcess(fields[8], numberList);

					if (temp.get("dcol_click_freqs") == null) {
						temp.put("dcol_click_freqs", dcol_click_freqs);
					} else {
						hashMapProcessSum((Map<String, Integer>) temp.get("dcol_click_freqs"), dcol_click_freqs);
					}

					Map<String, Integer> dcol_real_conv_freqs = hashMapProcess(fields[9], numberList);

					if (temp.get("dcol_real_conv_freqs") == null) {
						temp.put("dcol_real_conv_freqs", dcol_real_conv_freqs);
					} else {
						hashMapProcessSum((Map<String, Integer>) temp.get("dcol_real_conv_freqs"), dcol_real_conv_freqs);
					}

					Map<String, Integer> dcol_direct_conv_freqs = hashMapProcess(fields[10], numberList);

					if (temp.get("dcol_direct_conv_freqs") == null) {
						temp.put("dcol_direct_conv_freqs", dcol_direct_conv_freqs);
					} else {
						hashMapProcessSum((Map<String, Integer>) temp.get("dcol_direct_conv_freqs"), dcol_direct_conv_freqs);
					}

					Map<String, Integer> dcol_indirect_conv_freqs = hashMapProcess(fields[11], numberList);

					if (temp.get("dcol_indirect_conv_freqs") == null) {
						temp.put("dcol_indirect_conv_freqs", dcol_indirect_conv_freqs);
					} else {
						hashMapProcessSum((Map<String, Integer>) temp.get("dcol_indirect_conv_freqs"), dcol_indirect_conv_freqs);
					}

					allMap.put(key, temp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (String key : allMap.keySet()) {
				Map<String, Object> temp = allMap.get(key);
				

				fileWriterTest(path + "\\TEMP\\"+key, "csv", "freNum" + "\t" + "dcol_view_freqs" + "\t" + "dcol_click_freqs" + "\t" + "dcol_real_conv_freqs" + "\t" + "dcol_direct_conv_freqs" + "\t" +"dcol_indirect_conv_freqs" + "\t");				
				ArrayList<String> numberList = (ArrayList<String>) temp.get("number");
				for (String number : numberList) {
					String dcol_view_freqs = "0";
					try {
						dcol_view_freqs = String.valueOf(((Map<String, Integer>) temp.get("dcol_view_freqs")).get(number));
						if ("null".equals(dcol_view_freqs)) {
							dcol_view_freqs = "0";
						} else {
							dcol_view_freqs = dcol_view_freqs.replace(".0", "");
						}
					} catch (Exception e) {
					}
					String dcol_click_freqs = "0";
					try {
						dcol_click_freqs = String.valueOf(((Map<String, Integer>) temp.get("dcol_click_freqs")).get(number));
						if ("null".equals(dcol_click_freqs)) {
							dcol_click_freqs = "0";
						} else {
							dcol_click_freqs = dcol_click_freqs.replace(".0", "");
						}
					} catch (Exception e) {
					}
					String dcol_real_conv_freqs = "0";
					try {
						dcol_real_conv_freqs = String.valueOf(((Map<String, Integer>) temp.get("dcol_real_conv_freqs")).get(number));
						if ("null".equals(dcol_real_conv_freqs)) {
							dcol_real_conv_freqs = "0";
						} else {
							dcol_real_conv_freqs = dcol_real_conv_freqs.replace(".0", "");
						}
					} catch (Exception e) {
					}
					String dcol_direct_conv_freqs = "0";
					try {
						dcol_direct_conv_freqs = String.valueOf(((Map<String, Integer>) temp.get("dcol_direct_conv_freqs")).get(number));
						if ("null".equals(dcol_direct_conv_freqs)) {
							dcol_direct_conv_freqs = "0";
						} else {
							dcol_direct_conv_freqs = dcol_direct_conv_freqs.replace(".0", "");
						}
					} catch (Exception e) {
					}
					String dcol_indirect_conv_freqs = "0";
					try {
						dcol_indirect_conv_freqs = String.valueOf(((Map<String, Integer>) temp.get("dcol_indirect_conv_freqs")).get(number));
						if ("null".equals(dcol_indirect_conv_freqs)) {
							dcol_indirect_conv_freqs = "0";
						} else {
							dcol_indirect_conv_freqs = dcol_indirect_conv_freqs.replace(".0", "");
						}
					} catch (Exception e) {
					}
					fileWriterTest("D:\\SAMPLE\\TEMP\\"+key, "csv", number + "\t" + dcol_view_freqs + "\t" + dcol_click_freqs + "\t" + dcol_real_conv_freqs + "\t" + dcol_direct_conv_freqs + "\t" +dcol_indirect_conv_freqs + "\t");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void fileWriterTest(String fileName, String type, String txt) {
		try {

			// 파일 객체 생성
			File file = new File(fileName+"."+type);

			// true 지정시 파일의 기존 내용에 이어서 작성
			FileWriter fw = new FileWriter(file, true);

			// 파일안에 문자열 쓰기
			fw.write(txt + "\r\n");
			fw.flush();

			// 객체 닫기
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
