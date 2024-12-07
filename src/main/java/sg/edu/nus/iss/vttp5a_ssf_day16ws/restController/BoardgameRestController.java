package sg.edu.nus.iss.vttp5a_ssf_day16ws.restController;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.constants.Constant;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.model.Boardgame;
import sg.edu.nus.iss.vttp5a_ssf_day16ws.service.BoardgameRestService;

@RestController
@RequestMapping("/api/boardgame")
public class BoardgameRestController {

    @Autowired
    BoardgameRestService boardgameRestService;
    
    @PostMapping
    public ResponseEntity<String> allBoardgames() {
        JsonObject jObject = Json.createObjectBuilder()
                .add("insert_count", 1)
                .add("id", Constant.redisBoardgameKey)
                .build();

        List<Boardgame> boardgamesList = boardgameRestService.allBoardgames();
        ResponseEntity<String> res = ResponseEntity.status(HttpStatusCode.valueOf(201)).body(jObject.toString());
        // Use ResponseEntity<String>!
        return res;
    }

    @GetMapping("/{boardgameId}")
    public ResponseEntity<String> getBoardgamebyId(@PathVariable String boardgameId) {
        Optional<String> boardgameFound = boardgameRestService.getBoardgamebyId(Integer.parseInt(boardgameId));
        return boardgameFound.map(s -> new ResponseEntity<>(s, HttpStatusCode.valueOf(200)))
                .orElse(new ResponseEntity<>("No boardgame found!", HttpStatusCode.valueOf(404)));
    }

    @PutMapping("/{boardgameId}")
    public ResponseEntity<String> updateBoardgame(@PathVariable String boardgameId,
            @RequestBody String boardgameToUpdate, @RequestParam(required = false, defaultValue = "false") Boolean upsert) {
            
        ResponseEntity<String> res = null;

        Optional<String> boardgameFound = boardgameRestService.getBoardgamebyId(Integer.parseInt(boardgameId));
        if (boardgameFound.isPresent()){
            boardgameRestService.updateBoardgame(boardgameId, boardgameToUpdate);
            
            JsonObject jObject = Json.createObjectBuilder()
                    .add("update_count", 1)
                    .add("id", boardgameId)
                    .build();

            res = ResponseEntity.status(200).body(jObject.toString());
        } else if (upsert) {
            boardgameRestService.addNewBoardgame(boardgameId, boardgameToUpdate);
            res = ResponseEntity.ok().body("New boardgame added!");
        } else {
            res = ResponseEntity.status(400).body("Cannot find boardgame to update!");
        }
                      
        return res;
    }
}
