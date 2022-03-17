package lambda.layer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lambda.layer.data.access.MongoDBDAO;
import lambda.layer.data.access.SingletonMongoDBDAO;
import lambda.layer.data.access.dto.ModelDTO;

@Controller
public class WebController {

	private MongoDBDAO dao;

	public WebController() {
		dao = SingletonMongoDBDAO.getMongoDBDAO();
	}

	@RequestMapping("/online")
	public String online(Map<String, Object> model) {
		Map<String, Long> data = getOnlineModel();

		try {
			model.put("data", getJsonFromMap(data));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "view";
	}

	@RequestMapping("/offline")
	public String offline(Map<String, Object> model) {
		Map<String, Long> data = getOfflineModel();

		try {
			model.put("data", getJsonFromMap(data));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "view";

	}

	@RequestMapping("/lambda")
	public String lambda(Map<String, Object> model) {
		Map<String, Long> data = getLambdaModel();

		try {
			model.put("data", getJsonFromMap(data));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "view";
	}

	private JSONObject getJsonFromMap(Map<String, Long> map) throws JSONException {
		JSONObject jsonData = new JSONObject();
		for (String key : map.keySet()) {
			Long value = map.get(key);
			jsonData.put(key, value);
		}
		return jsonData;
	}

	// 4.1: Completar los métodos que devuelven los modelos y que son usados
	// más abajo para presentar los resultados

	@RequestMapping("/online_model")
	@ResponseBody
	public Map<String, Long> getOnlineModel() {
		Long timestamp = dao.getOfflineModel().getTimestamp();
		return getTags(dao.getOnlineModel(timestamp));
	}

	@RequestMapping("/offline_model")
	@ResponseBody
	public Map<String, Long> getOfflineModel() {
		List<ModelDTO> result = new ArrayList<>();
		result.add(dao.getOfflineModel());
		return getTags(result);
	}

	@RequestMapping("/lambda_model")
	@ResponseBody
	public Map<String, Long> getLambdaModel() {
		List<ModelDTO> result = new ArrayList<>();
		ModelDTO model = dao.getOfflineModel();
		result.add(model);
		result.addAll(dao.getOnlineModel(model.getTimestamp()));
		return getTags(result);
	}

	private Map<String, Long> getTags(List<? extends ModelDTO> lista) {
		Map<String, Long> result = new HashMap<>();

		for (ModelDTO model: lista) {
			List<String> hashtags = model.getClaves();
			List<Long> frequencies = model.getFrecuencias();
			for (int i = 0; i < hashtags.size(); i++) {
				String clave = hashtags.get(i);
				Long value = frequencies.get(i);
				if (result.containsKey(clave)) {
					result.put(clave, result.get(clave) + value);
				} else {
					result.put(clave, value);
				}
			}
		}

		return result;
	}

}