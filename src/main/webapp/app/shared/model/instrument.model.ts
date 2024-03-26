import { type IRobot } from '@/shared/model/robot.model';
import { type IInstrumentType } from '@/shared/model/instrument-type.model';

export interface IInstrument {
  id?: number;
  secCode?: string;
  robots?: IRobot[] | null;
  type?: IInstrumentType | null;
}

export class Instrument implements IInstrument {
  constructor(
    public id?: number,
    public secCode?: string,
    public robots?: IRobot[] | null,
    public type?: IInstrumentType | null,
  ) {}
}
