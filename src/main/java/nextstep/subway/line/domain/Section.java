package nextstep.subway.line.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
@Table(name = "section")
public class Section extends BaseEntity {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
	private Line line;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
	private Station upStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
	private Station downStation;

	@Column(name = "distance")
	private int distance;

	public void setLine(Line line) {
		this.line = line;
	}

	protected Section() {

	}

	private Section(Long id, Line line, Station upStation, Station downStation, int distance) {
		throwIfUpStationAndDownStationIsEqual(upStation, downStation);

		this.id = id;
		this.line = line;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	private void throwIfUpStationAndDownStationIsEqual(Station upStation, Station downStation) {
		if (upStation.equals(downStation)) {
			throw new IllegalArgumentException("상행역과 하행역은 같을 수 없습니다.");
		}
	}

	public static Section of(Long id, Station upStation, Station downStation, int distance) {
		return new Section(id, null, upStation, downStation, distance);
	}

	public static Section of(Station upStation, Station downStation, int distance) {
		return Section.of(null, upStation, downStation, distance);
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}
}