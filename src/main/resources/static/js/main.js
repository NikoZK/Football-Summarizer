const SERVER_URL = 'http://localhost:8080/api/v1/football/';

let currentFixtureId = null;
let matchDetailsModal = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    matchDetailsModal = new bootstrap.Modal(document.getElementById('matchDetailsModal'));

    // Set default date to yesterday
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    document.getElementById('match-date').valueAsDate = yesterday;

    // Load matches
    loadMatches();
});

function changeDate(days) {
    const dateInput = document.getElementById('match-date');
    const currentDate = new Date(dateInput.value);
    currentDate.setDate(currentDate.getDate() + days);
    dateInput.valueAsDate = currentDate;
    loadMatches();
}

async function loadMatches() {
    const dateInput = document.getElementById('match-date');
    const leagueSelector = document.getElementById('league-selector');
    const date = dateInput.value;
    const league = leagueSelector.value;
    const container = document.getElementById('matches-container');
    const spinner = document.getElementById('loading-spinner');

    spinner.style.display = 'block';
    container.innerHTML = '';

    try {
        const response = await fetch(`${SERVER_URL}matches?date=${date}&league=${league}`).then(handleHttpErrors);

        if (!response.matches || response.matches.length === 0) {
            container.innerHTML = `
                <div class="no-matches">
                    <i class="fas fa-calendar-times"></i>
                    <h3>No matches found</h3>
                    <p>Try selecting a different date</p>
                </div>
            `;
            return;
        }

        response.matches.forEach(match => {
            const matchCard = createMatchCard(match);
            container.appendChild(matchCard);
        });

    } catch (error) {
        container.innerHTML = `
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i>
                <h3>Error loading matches</h3>
                <p>${error.message}</p>
            </div>
        `;
    } finally {
        spinner.style.display = 'none';
    }
}

function createMatchCard(match) {
    const card = document.createElement('div');
    card.className = 'match-card';
    card.onclick = () => openMatchDetails(match.fixtureId);

    const statusClass = match.status === 'FT' ? 'finished' : match.status === 'LIVE' ? 'live' : 'scheduled';

    card.innerHTML = `
        <div class="match-header">
            <span class="badge bg-secondary">${match.league}</span>
            <span class="match-status ${statusClass}">${match.status}</span>
        </div>
        <div class="match-teams">
            <div class="team">
                <img src="${match.homeTeamLogo}" alt="${match.homeTeam}" class="team-logo">
                <div class="team-name">${match.homeTeam}</div>
                <div class="team-score">${match.homeScore !== null ? match.homeScore : '-'}</div>
            </div>
            <div class="match-vs">VS</div>
            <div class="team">
                <img src="${match.awayTeamLogo}" alt="${match.awayTeam}" class="team-logo">
                <div class="team-name">${match.awayTeam}</div>
                <div class="team-score">${match.awayScore !== null ? match.awayScore : '-'}</div>
            </div>
        </div>
        <div class="match-footer">
            <i class="fas fa-map-marker-alt me-2"></i>${match.venue}
        </div>
    `;

    return card;
}

async function openMatchDetails(fixtureId) {
    currentFixtureId = fixtureId;
    matchDetailsModal.show();



    const summarySection = document.getElementById('match-summary-section');
    const performanceButtons = document.getElementById('performance-buttons');
    const performanceSection = document.getElementById('player-performance-section');

    summarySection.innerHTML = `
        <div class="text-center my-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3">Loading match details...</p>
        </div>
    `;
    performanceButtons.style.display = 'none';
    performanceSection.innerHTML = '';

    try {
        const leagueSelector = document.getElementById('league-selector');
        const league = leagueSelector.value;
        const summary = await fetch(`${SERVER_URL}match/${fixtureId}/summary?league=${league}`).then(handleHttpErrors);
        displayMatchSummary(summary);

        // Only show performance buttons for completed matches (must have statistics)
        console.log('Match details:', {
            isUpcoming: summary.isUpcoming,
            hasStatistics: !!summary.statistics,
            hasPreview: !!summary.preview
        });

        if (!summary.isUpcoming && summary.statistics) {
            console.log('Showing performance buttons');
            performanceButtons.style.display = 'block';
        } else {
            console.log('Hiding performance buttons - upcoming match or no statistics');
            performanceButtons.style.display = 'none';
        }
    } catch (error) {
        performanceButtons.style.display = 'none';
        summarySection.innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Error loading match details: ${error.message}
            </div>
        `;
    }
}
function displayMatchSummary(summary) {
    const summarySection = document.getElementById('match-summary-section');

    // Upcoming match → brug preview i stedet
    if (summary.isUpcoming || !summary.statistics) {
        if (summary.preview) {
            displayMatchPreview(summary);
        } else {
            summarySection.innerHTML = `
                <div class="alert alert-info">
                    <i class="fas fa-info-circle me-2"></i>
                    This match hasn't been played yet. Details will be available after the match.
                </div>
            `;
        }
        return;
    }

    const stats = summary.statistics;
    const xgHome = stats.expectedGoalsHome != null ? stats.expectedGoalsHome.toFixed(2) : 'N/A';
    const xgAway = stats.expectedGoalsAway != null ? stats.expectedGoalsAway.toFixed(2) : 'N/A';

    summarySection.innerHTML = `
        <div class="match-details-header">
            <h2>${summary.homeTeam} ${summary.score} ${summary.awayTeam}</h2>
        </div>
        
        <div class="ai-summary">
            <h5><i class="fas fa-robot me-2"></i>AI Match Summary</h5>
            <p>${summary.aiSummary}</p>
        </div>

        <!-- Possession -->
        <div class="statistics-grid">
            <div class="stat-item-percent">
                <div class="stat-header">Possession</div>
                <div class="stat-values">
                    <div class="stat-percentages">
                        <span class="home-stat">${stats.possessionHome}%</span>
                        <span class="away-stat">${stats.possessionAway}%</span>
                    </div>
                    <div class="stat-bar">
                        <div class="stat-bar-fill home" style="width: ${stats.possessionHome}%"></div>
                        <div class="stat-bar-fill away" style="width: ${stats.possessionAway}%"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stat groups -->
        <div class="statistics-grid">

            <!-- Group 1: Attacking -->
            <div class="stat-group-wrapper attacking-group">
                <div class="stat-group">
                    <h5 class="stat-group-title"><i class="fas fa-bullseye me-2"></i>Attacking</h5>

                    <div class="stat-item">
                        <div class="stat-header">Expected Goals (xG)</div>
                        <div class="stat-values">
                            <span class="home-stat">${xgHome}</span>
                            <span class="stat-label">xG</span>
                            <span class="away-stat">${xgAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Total Shots</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.totalShotsHome}</span>
                            <span class="stat-label">Shots</span>
                            <span class="away-stat">${stats.totalShotsAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Shots on Goal</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.shotsOnGoalHome}</span>
                            <span class="stat-label">On Target</span>
                            <span class="away-stat">${stats.shotsOnGoalAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Corners Taken</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.cornersHome}</span>
                            <span class="stat-label">Corners</span>
                            <span class="away-stat">${stats.cornersAway}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Group 2: Defensive -->
            <div class="stat-group-wrapper defensive-group">
                <div class="stat-group">
                    <h5 class="stat-group-title"><i class="fas fa-shield-alt me-2"></i>Defensive</h5>

                    <div class="stat-item">
                        <div class="stat-header">Tackles</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.totalTacklesHome}</span>
                            <span class="stat-label">Total</span>
                            <span class="away-stat">${stats.totalTacklesAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Effective Tackles</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.effectiveTacklesHome}</span>
                            <span class="stat-label">Effective</span>
                            <span class="away-stat">${stats.effectiveTacklesAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Interceptions</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.interceptionsHome}</span>
                            <span class="stat-label">Intercepted</span>
                            <span class="away-stat">${stats.interceptionsAway}</span>
                        </div>
                    </div>
                    
                    <div class="stat-item">
                        <div class="stat-header">Saves Made</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.savesHome}</span>
                            <span class="stat-label">Saves</span>
                            <span class="away-stat">${stats.savesAway}</span>
                        </div>
                    </div>

                </div>
            </div>

            <!-- Group 3: Discipline -->
            <div class="stat-group-wrapper discipline-group">
                <div class="stat-group">
                    <h5 class="stat-group-title"><i class="fas fa-exclamation-triangle me-2"></i>Discipline</h5>

                    <div class="stat-item">
                        <div class="stat-header">Yellow Cards</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.yellowCardsHome}</span>
                            <span class="stat-label">
                                <i class="fas fa-square" style="color: #ffc107;"></i>
                            </span>
                            <span class="away-stat">${stats.yellowCardsAway}</span>
                        </div>
                    </div>

                    <div class="stat-item">
                        <div class="stat-header">Red Cards</div>
                        <div class="stat-values">
                            <span class="home-stat">${stats.redCardsHome}</span>
                            <span class="stat-label">
                                <i class="fas fa-square" style="color: #dc3545;"></i>
                            </span>
                            <span class="away-stat">${stats.redCardsAway}</span>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    `;
}



function displayMatchPreview(summary) {
    const summarySection = document.getElementById('match-summary-section');
    const preview = summary.preview;

    const streamingHtml = preview.streamingServices.map(service =>
        `<span class="badge bg-primary me-2 mb-2">${service}</span>`
    ).join('');

    // Build team form display
    let teamFormHtml = '';
    if (preview.homeForm && preview.awayForm) {
        const homeForm = preview.homeForm;
        const awayForm = preview.awayForm;

        teamFormHtml = `
            <div class="preview-card mb-4">
                <h5><i class="fas fa-trophy me-2"></i>Current Form & League Position</h5>
                <div class="row">
                    <div class="col-md-6 text-center">
                        <h6 class="text-primary">${summary.homeTeam}</h6>
                        ${homeForm.position ? `<p class="mb-1"><strong>Position:</strong> ${homeForm.position}</p>` : ''}
                        ${homeForm.points !== null ? `<p class="mb-1"><strong>Points:</strong> ${homeForm.points}</p>` : ''}
                        ${homeForm.lastFiveGames ? `
<div class="mb-2">
        <strong>Recent Form:</strong><br>
        ${formatFormBadges(homeForm.lastFiveGames)}
    </div>` : ''}
${homeForm.wins !== null ? `<p>W: ${homeForm.wins} | D: ${homeForm.draws} | L: ${homeForm.losses}</p>` : ''}
                    </div>
                    <div class="col-md-6 text-center border-start">
                        <h6 class="text-danger">${summary.awayTeam}</h6>
                        ${awayForm.position ? `<p class="mb-1"><strong>Position:</strong> ${awayForm.position}</p>` : ''}
                        ${awayForm.points !== null ? `<p class="mb-1"><strong>Points:</strong> ${awayForm.points}</p>` : ''}
                        ${awayForm.lastFiveGames ? `
    <div class="mb-2">
        <strong>Recent Form:</strong><br>
        ${formatFormBadges(awayForm.lastFiveGames)}
    </div>` : ''}
${awayForm.wins !== null ? `<p">W: ${awayForm.wins} | D: ${awayForm.draws} | L: ${awayForm.losses}</p>` : ''}

                    </div>
                </div>
            </div>
        `;
    }

    // Build additional info
    let additionalInfoHtml = '';
    const hasAdditionalInfo = preview.referee || preview.attendance;
    if (hasAdditionalInfo) {
        additionalInfoHtml = `
            <div class="preview-card mb-4">
                <h5><i class="fas fa-info-circle me-2"></i>Additional Information</h5>
                <div class="row">
                    ${preview.referee ? `
                    <div class="col-md-6">
                        <p class="mb-1"><i class="fas fa-user-tie me-2"></i><strong>Referee:</strong> ${preview.referee}</p>
                    </div>` : ''}
                    ${preview.attendance ? `
                    <div class="col-md-6">
                        <p class="mb-1"><i class="fas fa-users me-2"></i><strong>Stadium Capacity:</strong> ${preview.attendance.toLocaleString()}</p>
                    </div>` : ''}
                </div>
            </div>
        `;
    }

    summarySection.innerHTML = `
        <div class="match-details-header">
            <h2>${summary.homeTeam} vs ${summary.awayTeam}</h2>
            <span class="badge bg-info">Upcoming Match</span>
        </div>
        
        <div class="match-preview-section">
            <div class="row mb-4">
                <div class="col-md-6">
                    <div class="preview-card">
                        <h5><i class="fas fa-map-marker-alt me-2"></i>Venue</h5>
                        <p class="mb-1"><strong>${preview.stadium}</strong></p>
                        <p>${preview.city}</p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="preview-card">
                        <h5><i class="fas fa-calendar-alt me-2"></i>Date & Time</h5>
                        <p class="mb-1">${preview.date || 'TBD'}</p>
                        <p>${preview.time || 'TBD'}</p>
                    </div>
                </div>
            </div>
            
            <div class="preview-card mb-4">
                <h5><i class="fas fa-tv me-2"></i>Where to Watch</h5>
                <div>${streamingHtml}</div>
            </div>
            
            ${teamFormHtml}
            
            ${additionalInfoHtml}
            
            <div class="ai-summary">
                <h5><i class="fas fa-chart-line me-2"></i>Match Prediction</h5>
                <p>${preview.prediction}</p>
            </div>
        </div>
    `;
}

function formatFormBadges(formString) {
    if (!formString || formString === 'N/A') return '<span class="badge bg-secondary">N/A</span>';

    return formString.split('').map(result => {
        let badgeClass = 'bg-secondary';
        let icon = '';
        if (result === 'W') {
            badgeClass = 'bg-success';
            icon = '<i class="fas fa-check"></i>';
        } else if (result === 'D') {
            badgeClass = 'bg-warning';
            icon = '<i class="fas fa-minus"></i>';
        } else if (result === 'L') {
            badgeClass = 'bg-danger';
            icon = '<i class="fas fa-times"></i>';
        }
        return `<span class="badge ${badgeClass} me-1">${icon}</span>`;
    }).join('');
}

async function loadPlayerPerformance(type) {
    const performanceSection = document.getElementById('player-performance-section');

    performanceSection.innerHTML = `
        <div class="text-center my-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3">Analyzing player performance...</p>
        </div>
    `;

    try {
        const leagueSelector = document.getElementById('league-selector');
        const league = leagueSelector.value;
        const performance = await fetch(`${SERVER_URL}match/${currentFixtureId}/players?type=${type}&league=${league}`).then(handleHttpErrors);
        displayPlayerPerformance(performance);
    } catch (error) {
        performanceSection.innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Error loading player performance: ${error.message}
            </div>
        `;
    }
}

function displayPlayerPerformance(performance) {
    const performanceSection = document.getElementById('player-performance-section');
    const title = performance.type === 'best' ? 'Best Performers' : 'Worst Performers';
    const iconClass = performance.type === 'best' ? 'fa-star' : 'fa-thumbs-down';
    const badgeClass = performance.type === 'best' ? 'bg-success' : 'bg-danger';

    let html = `
        <div class="player-performance-header">
            <h4><i class="fas ${iconClass} me-2"></i>${title}</h4>
            <p>
                <i class="fas fa-info-circle me-1"></i>
                Note: Player ratings are estimated based on starting lineup and position. 
                ESPN API does not provide official match ratings for soccer.
            </p>
        </div>
        <div class="players-list">
    `;

    performance.players.forEach((player, index) => {
        html += `
            <div class="player-card">
                <div class="player-rank">#${index + 1}</div>
                <div class="player-info">
                    <div class="player-name">${player.name}</div>
                    <div class="player-details">
                        <span class="badge ${badgeClass}">Rating: ${player.rating.toFixed(1)}</span>
                        <span class="badge bg-secondary">${player.position}</span>
                        <span class="badge bg-info">${player.team}</span>
                        <span class="badge bg-dark">${player.minutes}'</span>
                    </div>
                    <div class="player-analysis">
                        <i class="fas fa-robot me-2"></i>${player.aiAnalysis}
                    </div>
                </div>
            </div>
        `;
    });

    html += '</div>';
    performanceSection.innerHTML = html;
}

async function handleHttpErrors(res) {
    if (!res.ok) {
        const errorResponse = await res.json().catch(() => ({ message: 'No error details provided' }));
        const msg = errorResponse.message || 'An error occurred';
        throw new Error(msg);
    }
    return res.json();
}