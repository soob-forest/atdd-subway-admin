package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.line.LineSectionAddAcceptanceMethods.*;
import static nextstep.subway.line.LineSectionDeleteAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 구간 제거 관련 기능")
public class LineSectionDeleteAcceptanceTest extends AcceptanceTest {
    private static final int DISTANCE = 10;

    private StationResponse 판교역;
    private StationResponse 정자역;
    private StationResponse 미금역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        판교역 = 지하철_역_등록되어_있음(StationRequest.from("판교역")).as(StationResponse.class);
        정자역 = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        미금역 = 지하철_역_등록되어_있음(StationRequest.from("미금역")).as(StationResponse.class);

        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   판교역.getId(),
                                                   정자역.getId(),
                                                   DISTANCE);

        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);

        SectionRequest 정자_미금_구간 = SectionRequest.of(정자역.getId(), 미금역.getId(), DISTANCE);
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 정자_미금_구간);
    }

    @DisplayName("노선 중간에 존재하는 역을 제거한다.")
    @Test
    void deleteSection1() {
        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 정자역.getId());

        // then
        지하철_노선에_지하철역_삭제됨(response);
    }

    @DisplayName("노선 상행 종점역을 제거한다.")
    @Test
    void deleteSection2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 판교역.getId());

        // then
        지하철_노선에_지하철역_삭제됨(response);
    }

    @DisplayName("노선 하행 종점역을 제거한다.")
    @Test
    void deleteSection3() {
        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 미금역.getId());

        // then
        지하철_노선에_지하철역_삭제됨(response);
    }

    @DisplayName("구간이 1개뿐인 노선에서는 역을 제거 할 수 없다.")
    @Test
    void deleteSection4() {
        // given
        지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 미금역.getId());

        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 정자역.getId());

        // then
        지하철_노선에_지하철역_삭제_실패(response);
    }

    @DisplayName("노선에 존재하지 않는 역은 제거 할 수 없다.")
    @Test
    void deleteSection5() {
        // given
        Station 강남역 = Station.from(10L, "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_노선의_지하철역_삭제_요청(신분당선.getId(), 강남역.getId());

        // then
        지하철_노선에_지하철역_삭제_실패(response);
    }
}