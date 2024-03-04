import { type IRobot } from '@/shared/model/robot.model';

export interface IInstrument {
  id?: number;
  secCode?: string | null;
  robots?: IRobot[] | null;
}

export class Instrument implements IInstrument {
  constructor(
    public id?: number,
    public secCode?: string | null,
    public robots?: IRobot[] | null,
  ) {}
}
