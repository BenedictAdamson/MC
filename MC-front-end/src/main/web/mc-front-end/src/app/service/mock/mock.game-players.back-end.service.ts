import { Observable, of } from 'rxjs';

import { AbstractGamePlayersBackEndService } from '../abstract.game-players.back-end.service';
import { GameIdentifier } from '../../game-identifier';
import { GamePlayers } from '../../game-players';

export class MockGamePlayersBackEndService extends AbstractGamePlayersBackEndService {

	private gamePlayers: GamePlayers;
	private game: GameIdentifier;

	constructor(
		gamePlayersServer: GamePlayers,
		private self: string
	) {
		super();
		this.game = gamePlayersServer.game;
		this.gamePlayers = gamePlayersServer;
	};


	get(game: GameIdentifier): Observable<GamePlayers | null> {
		this.expectGameIdentifier(game, 'getGamePlayers');
		return of(this.gamePlayers);
	}

	joinGame(game: GameIdentifier): Observable<GamePlayers> {
		this.expectGameIdentifier(game, 'joinGame');
		if (!this.gamePlayers.isPlaying(this.self)) {
			this.gamePlayers.users.set('FIXME', this.self);
		}
		return this.copy();
	}

	endRecruitment(game: GameIdentifier): Observable<GamePlayers> {
		this.expectGameIdentifier(game, 'endRecuitment');
		this.gamePlayers.recruiting = false;
		return this.copy();
	}

	private expectGameIdentifier(game: GameIdentifier, method: string): void {
		expect(game).withContext('MockGamePlayersService.' + method + '(game)').toEqual(this.game);
	}

	private copy(): Observable<GamePlayers> {
		return of(new GamePlayers(this.gamePlayers.game, this.gamePlayers.recruiting, this.gamePlayers.users));
	}
}
