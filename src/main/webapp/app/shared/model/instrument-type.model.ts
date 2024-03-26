import { type IInstrument } from '@/shared/model/instrument.model';

export interface IInstrumentType {
  id?: number;
  name?: string;
  instruments?: IInstrument[] | null;
}

export class InstrumentType implements IInstrumentType {
  constructor(
    public id?: number,
    public name?: string,
    public instruments?: IInstrument[] | null,
  ) {}
}
