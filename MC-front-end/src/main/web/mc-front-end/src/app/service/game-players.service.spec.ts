import { v4 as uuid } from 'uuid';

import { HttpClient } from '@angular/common/http';

import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';

import { AbstractGamePlayersBackEndService } from './abstract.game-players.back-end.service';
import { HttpGamePlayersBackEndService, getApiGamePlayersPath, getApiJoinGamePath, getApiGameEndRecuitmentPath } from './http.game-players.back-end.service';
import { GamePlayers } from '../game-players'
import { GameIdentifier } from '../game-identifier'
import { GamePlayersService } from './game-players.service';


describe('GamePlayersService', () => {
	let httpTestingController: HttpTestingController;

	const SCENARIO_A: string = uuid();
	const SCENARIO_B: string = uuid();
	const USER_ID_A: string = uuid();
	const USER_ID_B: string = uuid();
	const CREATED_A: string = '1970-01-01T00:00:00.000Z';
	const CREATED_B: string = '2020-12-31T23:59:59.999Z';
	const GAME_IDENTIFIER_A: GameIdentifier = { scenario: SCENARIO_A, created: CREATED_A };
	const GAME_IDENTIFIER_B: GameIdentifier = { scenario: SCENARIO_B, created: CREATED_B };
	const GAME_PLAYERS_A: GamePlayers = { game: GAME_IDENTIFIER_A, recruiting: true, users: [USER_ID_A, USER_ID_B] };
	const GAME_PLAYERS_B: GamePlayers = { game: GAME_IDENTIFIER_B, recruiting: false, users: [] };

	const setUp = function(): GamePlayersService {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule]
		});

		const httpClient: HttpClient = TestBed.inject(HttpClient);
		httpTestingController = TestBed.inject(HttpTestingController);
		const backEnd: AbstractGamePlayersBackEndService = new HttpGamePlayersBackEndService(httpClient);
		return new GamePlayersService(backEnd);
	};

	it('should be created', () => {
		const service: GamePlayersService = setUp();
		expect(service).toBeTruthy();
	});

	const testGet = function(gamePlayers: GamePlayers) {
		const expectedPath: string = getApiGamePlayersPath(gamePlayers.game);
		const service: GamePlayersService = setUp();

		service.get(gamePlayers.game).subscribe(g => expect(g).toEqual(gamePlayers));

		const request = httpTestingController.expectOne(expectedPath);
		expect(request.request.method).toEqual('GET');
		request.flush(gamePlayers);
		httpTestingController.verify();
	};

	it('can get game players [A]', () => {
		testGet(GAME_PLAYERS_A);
	})

	it('can get game players [B]', () => {
		testGet(GAME_PLAYERS_B);
	})


	const testJoinGame = function(done: any, gamePlayers0: GamePlayers, user: string) {
		const game: GameIdentifier = gamePlayers0.game;
		const expectedPath: string = getApiJoinGamePath(game);
		var users: string[] = gamePlayers0.users;
		users.push(user);
		// Tough test: the reply identifier is not the same object
		const gamePlayers1: GamePlayers = {
			game: { scenario: game.scenario, created: game.created },
			recruiting: gamePlayers0.recruiting,
			users: users
		};
		const service: GamePlayersService = setUp();

		service.joinGame(game);

		const request = httpTestingController.expectOne(expectedPath);
		expect(request.request.method).toEqual('POST');
		request.flush(gamePlayers1);
		httpTestingController.verify();

		service.get(game).subscribe({
			next: (gps) => {
				expect(gps).withContext('gamePlayers').not.toBeNull();
				expect(gps).withContext('gamePlayers').toEqual(gamePlayers1);
				done();
			}, error: (e) => { fail(e); }, complete: () => { }
		});
	}

	it('can join game [A]', (done) => {
		testJoinGame(done, GAME_PLAYERS_A, USER_ID_A);
	})

	it('can join game [B]', (done) => {
		testJoinGame(done, GAME_PLAYERS_B, USER_ID_B);
	})


	const testEndRecuitment = function(done: any, gamePlayers0: GamePlayers) {
		const game: GameIdentifier = gamePlayers0.game;
		const path: string = getApiGameEndRecuitmentPath(game);
		const service: GamePlayersService = setUp();
		// Tough test: the reply identifier is not the same object
		const gamePlayersReply: GamePlayers = {
			game: { scenario: game.scenario, created: game.created },
			recruiting: false,
			users: gamePlayers0.users
		}

		service.endRecruitment(game);

		const request = httpTestingController.expectOne(path);
		expect(request.request.method).toEqual('POST');
		request.flush(gamePlayersReply);
		httpTestingController.verify();

		service.get(game).subscribe({
			next: (gps) => {
				expect(gps).withContext('gamePlayers').not.toBeNull();
				expect(gps).withContext('gamePlayers').toEqual(gamePlayersReply);
				done();
			}, error: (e) => { fail(e); }, complete: () => { }
		});
	}

	it('can end recuitment [A]', (done) => {
		testEndRecuitment(done, GAME_PLAYERS_A);
	})

	it('can end recuitment [B]', (done) => {
		testEndRecuitment(done, GAME_PLAYERS_B);
	})



	const testGetAfterUpdate = function(gamePlayers: GamePlayers) {
		// Tough test: use two identifiers that are semantically equivalent, but not the same object.
		const game1: GameIdentifier = gamePlayers.game;
		const game2: GameIdentifier = { scenario: game1.scenario, created: game1.created };
		const expectedPath: string = getApiGamePlayersPath(game1);
		const service: GamePlayersService = setUp();

		service.update(game1);
		service.get(game2).subscribe(g => expect(g).toEqual(gamePlayers));

		// Only one GET expected because should use the cached value.
		const request = httpTestingController.expectOne(expectedPath);
		expect(request.request.method).toEqual('GET');
		request.flush(gamePlayers);
		httpTestingController.verify();
	};

	it('can get game players after update game players [A]', () => {
		testGetAfterUpdate(GAME_PLAYERS_A);
	})

	it('can get game players after update game players [B]', () => {
		testGetAfterUpdate(GAME_PLAYERS_B);
	})



	const testUpdateAfterGet = function(gamePlayers: GamePlayers) {
		const game: GameIdentifier = gamePlayers.game;
		const expectedPath: string = getApiGamePlayersPath(game);
		const service: GamePlayersService = setUp();

		service.get(game).subscribe(g => expect(g).toEqual(gamePlayers));
		service.update(game);

		const requests: TestRequest[] = httpTestingController.match(expectedPath);
		expect(requests.length).withContext('number of requests').toEqual(2);
		expect(requests[0].request.method).toEqual('GET');
		requests[0].flush(gamePlayers);
		expect(requests[1].request.method).toEqual('GET');
		requests[1].flush(gamePlayers);
		httpTestingController.verify();
	};

	it('can update game players after get game players [A]', () => {
		testUpdateAfterGet(GAME_PLAYERS_A);
	})

	it('can update game players after get game players [B]', () => {
		testUpdateAfterGet(GAME_PLAYERS_B);
	})



	const testGetForChangingValue = function(
		done: any,
		game: GameIdentifier,
		recruiting1: boolean,
		users1: string[],
		recruiting2: boolean,
		users2: string[]
	) {
		const gamePlayers1: GamePlayers = { game: game, recruiting: recruiting1, users: users1 };
		const gamePlayers2: GamePlayers = { game: game, recruiting: recruiting2, users: users2 };
		const expectedPath: string = getApiGamePlayersPath(game);
		const service: GamePlayersService = setUp();
		var n: number = 0;

		service.get(game).subscribe(
			gamePlayers => {
				expect(0 != n || gamePlayers1 == gamePlayers).withContext('provides the first value').toBeTrue();
				expect(1 != n || gamePlayers2 == gamePlayers).withContext('provides the second value').toBeTrue();
				n++;
				if (n == 2) done();
			}
		);
		service.update(game);

		const requests: TestRequest[] = httpTestingController.match(expectedPath);
		expect(requests.length).withContext('number of requests').toEqual(2);
		expect(requests[0].request.method).toEqual('GET');
		requests[0].flush(gamePlayers1);
		expect(requests[1].request.method).toEqual('GET');
		requests[1].flush(gamePlayers2);
		httpTestingController.verify();
	};

	it('provides updated game players [A]', (done) => {
		testGetForChangingValue(done, GAME_IDENTIFIER_A, true, [], false, [USER_ID_A]);
	})

	it('provides updated game players [B]', (done) => {
		testGetForChangingValue(done, GAME_IDENTIFIER_B, true, [USER_ID_A], true, [USER_ID_B]);
	})



	const testGetForUnchangedUpdate = function(gamePlayers: GamePlayers) {
		const game: GameIdentifier = gamePlayers.game;
		const expectedPath: string = getApiGamePlayersPath(game);
		const service: GamePlayersService = setUp();
		var n: number = 0;

		service.get(game).subscribe(
			gps => {
				expect(gamePlayers == gps).withContext('provides the expected value').toBeTrue();
				n++;
				expect(n).withContext('number emitted').toEqual(1);
			}
		);
		service.update(game);

		const requests: TestRequest[] = httpTestingController.match(expectedPath);
		expect(requests.length).withContext('number of requests').toEqual(2);
		expect(requests[0].request.method).toEqual('GET');
		requests[0].flush(gamePlayers);
		expect(requests[1].request.method).toEqual('GET');
		requests[1].flush(gamePlayers);
		httpTestingController.verify();
	};

	it('provides distinct game players [A]', () => {
		testGetForUnchangedUpdate(GAME_PLAYERS_A);
	})

	it('provides distinct game players [B]', () => {
		testGetForUnchangedUpdate(GAME_PLAYERS_B);
	})
});
