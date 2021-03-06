import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from '../service/user.service';
import { UserDetails } from '../user-details';

@Component({
	selector: 'app-add-user',
	templateUrl: './add-user.component.html',
	styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

	constructor(
		private readonly router: Router,
		private readonly service: UserService
	) { }

	ngOnInit(): void {
		this.userDetails = { username: "", password: "", authorities: [] };
	}

	userDetails: UserDetails = { username: "", password: "", authorities: [] };

	/**
	 * Whether this user has been explicitly rejected by the server.
	 * This will be false if the user have not *yet* been submitted to the server.
	 */
	rejected: boolean = false;

	/**
	 * @description
	 * Attempts to add a user using the #username and #password,
	 * through the UserService associated with this component.
	 */
	add(): void {
		this.service.add(this.userDetails).subscribe({
			next: () => {
				this.rejected = false;
				this.router.navigateByUrl('/user')
			},
			error: () => {
				this.rejected = true;
			},
		});
	}
}
