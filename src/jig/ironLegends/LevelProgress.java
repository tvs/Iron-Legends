package jig.ironLegends;

public class LevelProgress {
	double m_introRemainingMs;
	boolean m_bExitActivated;
	boolean m_bExitComplete;
	protected int m_levelScore;

	public LevelProgress() {
		reset();
	}

	public double introRemaining() {
		return m_introRemainingMs;
	}

	public boolean isIntro() {
		return m_introRemainingMs > 0;
	}

	public void setIntro(double introMs) {
		m_introRemainingMs = introMs;
	}

	public void setExitComplete(boolean bComplete) {
		m_bExitComplete = bComplete;
	}

	public void setExit(boolean bExit) {
		if (bExit) {
			m_bExitActivated = true;
			m_bExitComplete = false;
		} else {
			m_bExitActivated = false;
			m_bExitComplete = true;
		}
	}

	public boolean isExitActivated() {
		return m_bExitActivated;
	}

	public boolean isExitComplete() {
		return m_bExitComplete;
	}

	public void update(double deltaMs) {
		if (m_introRemainingMs > 0) {
			m_introRemainingMs -= deltaMs;
		}
	}

	public void reset() {
		m_levelScore = 0;
		m_introRemainingMs = 0;
		m_bExitActivated = false;
		m_bExitComplete = false;
	}

	public int getScore() {
		return m_levelScore;
	}
	
	public void setScore(int score) {
		m_levelScore = score;
	}
}
