import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Jjung 을 위해 만든 하드코딩
 * @author bymin
 *
 */
public class FileAnalyzer {

	private static final String GUBUN = "_^_"; // 구분자값
	
	private static final String CVS_SPLIT = "\t";	// cvs 분리 툴 (탭)
	private static final String CVS_GUBUN = ",";	// cvs 분리 툴 (탭)
	
	private static final String PATH = "D:\\SAMPLE\\"; // 탐색할 파일 경로
	private static final String OUTPUT_TYPE = "TEMP\\temp_1.txt";  // OUT 할 파일 경로 (파일명포함)
	private static final String ORIGIN_FILE = "20180706_.txt";		// INPUT 할 파일명
	
	// 분석할 메뉴명
	private static final String[] menu =  {"날짜", "광고주명", "광고구분", "웹모바일","프리퀀시","노출수","클릭수","실시간","직접","간접"}; // "프리퀀시", 

	private static final int[] KEY_LIST = {0,1,2,3};  
	private static final int[] CALCULATION_LIST = {4,5,6,7,8};  
	
	private static final int NUMBER_LIST = -1;  
			
	// Gson 으로 json 을 분석하겠다!
	Gson gson = new Gson();
	
	// 모든 정보를 Map 에 담겠다!
	Map<String, Map<Integer, Object>> allMap = new HashMap<String, Map<Integer, Object>>();
	
	/**
	 * 모든 것의 시작 psvm
	 * @param args
	 */
	public static void main(String[] args) {
		FileAnalyzer hb = new FileAnalyzer();
		hb.run(PATH+ORIGIN_FILE, "euc-kr");
	}

	private Map<Integer, Integer> hashMapProcess(String json, ArrayList<Integer> numberList) {
		try {
			json = json.replaceAll("\\\"", "");
			if ("\\N".equals(json)) {
				return null;
			}
			Map<Integer, String> temp = new HashMap<>();
			Map<Integer, Integer> process = new HashMap<>();
			temp = (Map<Integer, String>) gson.fromJson(json, temp.getClass());
			for (Integer key : temp.keySet()) {
				if (!numberList.contains(key)) {
					numberList.add(key);
				}
				process.put(key, Integer.valueOf(String.valueOf(temp.get(key)).replace(".0", "")));
			}
			return process;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 중복되는 요청이 없음.
	private void hashMapProcessSum(Map<Integer, Integer> processOrigin, Map<Integer, Integer> processNew) {
		for (Integer key : processNew.keySet()) {
			if (processOrigin.get(key) != null) {
				processOrigin.put(key, processOrigin.get(key) + processNew.get(key));
			} else {
				processOrigin.put(key, processNew.get(key));
			}
		}
	}

	private void run(String path, String encoding) {
		BufferedReader br = null;
		String line;
		StringBuilder startTab = new StringBuilder();
		for (int i = 0; i < menu.length; i++) {
			startTab.append(menu[i]).append(CVS_GUBUN);
		}
		fileWriterTest(PATH + OUTPUT_TYPE, "csv", startTab.toString());				
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(CVS_SPLIT);
				try {
					
					StringBuilder keyTemp = new StringBuilder();
					for (int key : KEY_LIST) {
						keyTemp.append(fields[key]).append(GUBUN);
					}
					
					String key = keyTemp.toString();
					Map<Integer, Object> temp = allMap.get(key);

					if (allMap.get(key) == null) {
						temp = new HashMap<Integer, Object>();
					}

					ArrayList<Integer> numberList = (ArrayList<Integer>) temp.get(NUMBER_LIST);
					if (temp.get(NUMBER_LIST) == null) {
						numberList = new ArrayList<>();
						temp.put(NUMBER_LIST, numberList);
					}
					
					for (int calculation : CALCULATION_LIST) {
						Map<Integer, Integer> tempFields = hashMapProcess(fields[calculation], numberList);

						if (temp.get(calculation) == null) {
							temp.put(calculation, tempFields);
						} else if (tempFields != null){
							hashMapProcessSum((Map<Integer, Integer>) temp.get(calculation), tempFields);
						}
					}

					allMap.put(key, temp);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (String key : allMap.keySet()) {
				Map<Integer, Object> temp = allMap.get(key);
				ArrayList<String> numberList = (ArrayList<String>) temp.get(NUMBER_LIST);
				Collections.sort(numberList, new Comparator<String>(){
				      public int compare(String obj1, String obj2) {
				            return (Integer.parseInt(obj1) <Integer.parseInt(obj2)) ? -1: (Integer.parseInt(obj1) > Integer.parseInt(obj2)) ? 1:0 ;
				      }
				});

				for (String number : numberList) {
					String[] tempStr = new String[CALCULATION_LIST.length];
					
					int cnt = 0;

					for (int calculation : CALCULATION_LIST) {
						tempStr[cnt] = String.valueOf(((Map<String, Integer>) temp.get(menu[calculation])).get(number));
						if ("null".equals(tempStr[cnt])) {
							tempStr[cnt] = "0";
						} else {
							tempStr[cnt] = tempStr[cnt].replace(".0", "");
						}
						cnt++;
					}
					
					StringBuilder freq = new StringBuilder();
					for (String string : tempStr) {
						freq.append(string).append(CVS_GUBUN);
					}
					
					fileWriterTest(PATH + OUTPUT_TYPE,"csv", key.replace(GUBUN, CVS_GUBUN)+CVS_GUBUN+ number + CVS_GUBUN + freq.toString());
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
		System.out.println("끝");
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
