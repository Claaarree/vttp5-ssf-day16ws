package sg.edu.nus.iss.vttp5a_ssf_day16ws.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.constants.Constant;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.model.Boardgame;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.repository.HashRepo;

@Service
public class BoardgameRestService {

    @Autowired
    HashRepo boardgameRepo;

    public List<Boardgame> allBoardgames() {
        JsonArray jArray = null;
        try {
            InputStream is = new FileInputStream(new File(".\\src\\main\\resources\\static\\JSON\\game.json"));
            JsonReader jReader = Json.createReader(is);
            jArray = jReader.readArray();
        } catch (FileNotFoundException e) {
            // day 6 slides??
            e.printStackTrace();
        }

        List<Boardgame> boardgamesList = new ArrayList<>();
        for (int i = 0; i< jArray.size(); i++) {
            JsonObject jObject = jArray.getJsonObject(i);
            Boardgame b = new Boardgame();
            // if it's parsable it will if not then it won't?
            b.setId(jObject.getInt("gid"));
            b.setName(jObject.getString("name"));
            b.setYear(jObject.getInt("year"));
            b.setRanking(jObject.getInt("ranking"));
            b.setUsersRated(jObject.getInt("users_rated"));
            b.setUrl(jObject.getString("url"));
            b.setImage(jObject.getString("image"));

            // adding to redis below
            boardgameRepo.addToHash(Constant.redisBoardgameKey, String.valueOf(b.getId()), jObject.toString());
            // do I use jObject.toString()? or b.toString?
            // use jObject.toString()

            boardgamesList.add(b);
        }
        return boardgamesList;
    }

    public Optional<String> getBoardgamebyId(Integer boardgameId) {
        Optional<String> opt = Optional.ofNullable(boardgameRepo.getFieldValue(Constant.redisBoardgameKey, String.valueOf(boardgameId)));
        // Boardgame b = new Boardgame();
        // if (opt.isPresent()){
        //     JsonReader jReader = Json.createReader(new StringReader(opt.get()));
        //     JsonObject jObject = jReader.readObject();
        //     b.setId(jObject.getInt("gid"));
        //     b.setName(jObject.getString("name"));
        //     b.setYear(jObject.getInt("year"));
        //     b.setRanking(jObject.getInt("ranking"));
        //     b.setUsersRated(jObject.getInt("users_rated"));
        //     b.setUrl(jObject.getString("url"));
        //     b.setImage(jObject.getString("image"));

        // }
        
        return opt;
    }

    public void updateBoardgame(String boardgameId, String boardgameToUpdate) {
        boardgameRepo.editFieldValue(Constant.redisBoardgameKey, boardgameId, boardgameToUpdate);
    }

    public void addNewBoardgame(String boardgameId, String boardgameToUpdate) {
        boardgameRepo.addToHash(Constant.redisBoardgameKey, boardgameId, boardgameToUpdate);
    }
    
}
