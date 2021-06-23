package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 판교역;
    private StationResponse 정자역;
    private HashMap<String, String> createParams;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given 신분당선에 강남역 - 광교역이 등록되어있다.
        강남역 = StationAcceptanceTest.requestCreateStation("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.requestCreateStation("광교역").as(StationResponse.class);
        판교역 = StationAcceptanceTest.requestCreateStation("판교역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.requestCreateStation("정자역").as(StationResponse.class);

        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 7 + "");
        신분당선 = LineAcceptanceTest.requestCreateLine(createParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 역_사이에_새로운_역을_등록할경우_상행종점이_같을때() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 판교역, 4);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then 역순서 확인 강남역-광교역 + 강남역-판교역 = 강남역-판교역-광교역
        등록된_역_순서_확인(Arrays.asList(강남역, 판교역, 광교역));
    }

    @Test
    void 새로운_역을_상행종점으로_등록할때() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(판교역, 강남역, 4);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then 역순서 확인 강남역-광교역 + 판교역-강남역 = 판교역-강남역-광교역
        등록된_역_순서_확인(Arrays.asList(판교역, 강남역, 광교역));
    }

    @Test
    void 새로운_역을_하행종점으로_등록할때() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(광교역, 정자역, 4);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then 역순서 확인 강남역-광교역 + 광교역-정자역 = 강남역-광교역-정자역
        등록된_역_순서_확인(Arrays.asList(강남역, 광교역, 정자역));
    }

    @Test
    void 역_사이에_새로운_역을_등록할경우_새로운구간의_거리가_기존거리의_길이보다_크거나_같을때_실패() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 판교역, 7);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 상행역_하행역이_기존에_등록되어있는경우_실패() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 광교역, 7);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 상행역_하행역_둘다_기존역에_없는경우_실패() {
        // when 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(판교역, 정자역, 7);
        // then 지하철_노선에_지하철역_등록됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 노선의_구간을_삭제한다() {
        //given 강남역 - 판교역 - 광교역이 등록되어있다.
        지하철_노선에_지하철역_등록_요청(강남역, 판교역, 4);
        // when 지하철_노선에_지하철역_삭제_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(판교역);
        // then 지하철_노선에_자하철역_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then 역순서 확인 (강남역-판교역-광교역) - 판교역 = 강남역-광교역
        등록된_역_순서_확인(Arrays.asList(강남역, 광교역));
    }

    @Test
    void 노선의_존재하지않는_역을_삭제할경우_실패() {
        //given 강남역 - 판교역 - 광교역이 등록되어있다.
        지하철_노선에_지하철역_등록_요청(강남역, 판교역, 4);
        // when 지하철_노선에_지하철역_삭제_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(정자역);
        // then 지하철_노선에_자하철역_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 노선의_마지막구간을_삭제할경우_실패() {
        //given 강남역 - 광교역이 등록되어있다.
        // when 지하철_노선에_지하철역_삭제_요청
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(강남역);
        // then 지하철_노선에_자하철역_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_삭제_요청(StationResponse station) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", 신분당선.getId(), station.getId())
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStation.getId() + "");
        params.put("downStationId", downStation.getId() + "");
        params.put("distance", distance + "");
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + 신분당선.getId() + "/sections")
                .then().log().all().extract();
    }

    private void 등록된_역_순서_확인(List<StationResponse> stationList) {
        List<Long> expectedLineIds = stationList.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        ExtractableResponse<Response> lineResponse = RestAssured
                .given().log().all()
                .when().get("lines/1")
                .then().log().all().extract();

        List<Long> actualLineIds = lineResponse.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(actualLineIds).isEqualTo(expectedLineIds);
    }
}