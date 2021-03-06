import { Observable } from 'rxjs';

import { Injectable } from '@angular/core';

import { AbstractGameBackEndService } from './abstract.game.back-end.service';
import { CachingKeyValueService } from './caching.key-value.service';
import { Game } from '../game'
import { GameIdentifier } from '../game-identifier'


@Injectable({
	providedIn: 'root'
})
export class GameService extends CachingKeyValueService<GameIdentifier, Game, string> {

	static getApiGamesPath(scenario: string): string {
		return '/api/scenario/' + scenario + '/game/';
	}

	static getApiGamePath(id: GameIdentifier): string {
		return GameService.getApiGamesPath(id.scenario) + id.created;
	}

	constructor(
		backEnd: AbstractGameBackEndService
	) {
		super(backEnd);
	}
	
	/**
	 * Create a new game for a given scenario.
	 *
	 * @param scenario
	 * The unique ID of the scenario for which to create a gave.
	 * @returns
	 * An [[Observable]] that provides the created game.
	 * The [[GameIdentifier.scenario]] of the [[Game.identifier]] of the created game
	 * is equal to the given {@code scenario}.
	 */
	createGame(scenario: string): Observable<Game> {
		/* The server actually replies to the POST with a 302 (Found) redirect to the resource of the created game.
		 * The HttpClient or browser itself handles that redirect for us.
		 */
		return this.add(scenario) as Observable<Game>;
	}

	getAll(): undefined {
		return undefined;
	}

	protected createKeyString(id: GameIdentifier): string {
		return id.scenario + '/' + id.created;
	}

	protected getKey(value: Game): GameIdentifier {
		return value.identifier;
	}
}
