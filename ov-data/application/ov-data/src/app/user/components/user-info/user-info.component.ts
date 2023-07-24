import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ApiService} from "../../../core/services/api.service";
import {Address, Child, Parent} from "../../../models";

import {FormControl, FormGroup, NonNullableFormBuilder} from "@angular/forms";

interface ParentForm {
    firstName: FormControl<string>;
    lastName: FormControl<string>;
    email: FormControl<string>;
    phone: FormControl<string>;
    registerNr: FormControl<string>;
}

interface AddressForm {
    street: FormControl<string>;
    houseNr: FormControl<string>;
    commune: FormControl<string>;
    postalCode: FormControl<string>;
}

interface ChildForm {
    firstName: FormControl<string>;
    lastName: FormControl<string>;
    group: FormControl<string>;
    registerNr: FormControl<string>;
}

@Component({
    selector: 'app-user-info',
    templateUrl: './user-info.component.html',
    styleUrls: ['./user-info.component.scss']
})
export class UserInfoComponent implements OnChanges {

    @Input() parent1: Parent | undefined = undefined;
    @Input() parent2: Parent | undefined = undefined;
    @Input() address: Address | undefined = undefined;
    @Input() child: Child | undefined = undefined;

    parent1Form: FormGroup<ParentForm>;
    parent2Form: FormGroup<ParentForm>;
    addressForm: FormGroup<AddressForm>;
    childForm: FormGroup<ChildForm>;

    constructor(private api: ApiService, private fb: NonNullableFormBuilder) {

        this.parent1Form = this.fb.group({
            firstName: "",
            lastName: "",
            email: "",
            phone: "",
            registerNr: ""
        });
        this.parent2Form = this.fb.group({
            firstName: "",
            lastName: "",
            email: "",
            phone: "",
            registerNr: ""
        });
        this.addressForm = this.fb.group({
            street: "",
            houseNr: "",
            commune: "",
            postalCode: ""
        });
        this.childForm = this.fb.group({
            firstName: "",
            lastName: "",
            group: "",
            registerNr: ""
        });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['parent1'] && !!this.parent1) {
            this.parent1Form.reset(this.parent1);
        }
        if (changes['parent2'] && !!this.parent2) {
            this.parent2Form.reset(this.parent2);
        }
        if (changes['address'] && !!this.address) {
            this.addressForm.reset(this.address);
        }
        if (changes['child'] && !!this.child) {
            this.childForm.reset(this.child);
        }
    }

    updateData() {

    }
}
